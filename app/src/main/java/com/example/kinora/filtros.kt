package com.example.kinora

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf

class filtros : BottomSheetDialogFragment() {

    private lateinit var scrollContent: NestedScrollView
    private lateinit var navClasificacion: TextView
    private lateinit var navGenero: TextView
    private lateinit var navTipo: TextView

    private lateinit var labelClasificacion: View
    private lateinit var labelGenero: View
    private lateinit var labelTipo: View


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)

        dialog.setOnShowListener { dlg ->
            val d = dlg as BottomSheetDialog

            d.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            d.window?.setDimAmount(0f)

            d.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            bottomSheet?.background = null

            val coordinator = d.findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            coordinator?.setBackgroundColor(Color.TRANSPARENT)
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()

        val bottomSheetDialog = dialog as? BottomSheetDialog ?: return

        dialog?.window?.setDimAmount(0f)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let { sheet ->
            val params = sheet.layoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            sheet.layoutParams = params

            val behavior = BottomSheetBehavior.from(sheet)
            behavior.isHideable = true
            behavior.peekHeight = resources.displayMetrics.heightPixels
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            try {
                behavior.isFitToContents = false
                behavior.expandedOffset = 0
            } catch (ignored: Exception) { /* ignora si no existe */ }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.filtros, container, false)

        scrollContent = view.findViewById(R.id.scroll_content)
        navClasificacion = view.findViewById(R.id.nav_clasificacion)
        navGenero = view.findViewById(R.id.nav_genero)
        navTipo = view.findViewById(R.id.nav_tipo)

        labelClasificacion = view.findViewById(R.id.label_clasificacion)
        labelGenero = view.findViewById(R.id.label_genero)
        labelTipo = view.findViewById(R.id.label_tipo)

        view.post { setupScrollListener() }
        cargarFiltrosDesdeServidor()


        view?.findViewById<View>(R.id.btn_restablecer)?.setOnClickListener {
            limpiarSeleccion()
        }

        view?.findViewById<View>(R.id.btn_aplicar)?.setOnClickListener {
            aplicarFiltrosYEnviarResultado()
        }

        return view
    }

    private fun setupScrollListener() {
        scrollContent.setOnScrollChangeListener { _, _, _, _, _ ->
            val visibleClas = visiblePercent(labelClasificacion)
            val visibleGen = visiblePercent(labelGenero)
            val visibleTipo = visiblePercent(labelTipo)

            val max = maxOf(visibleClas, visibleGen, visibleTipo)

            when (max) {
                visibleClas -> highlightNav(navClasificacion)
                visibleGen -> highlightNav(navGenero)
                visibleTipo -> highlightNav(navTipo)
            }
        }
    }

    private fun visiblePercent(v: View): Int {
        val scrollBounds = Rect()
        scrollContent.getHitRect(scrollBounds)

        val rect = Rect()
        return if (!v.getLocalVisibleRect(rect)) {
            0
        } else {
            (rect.height() * 100) / v.height
        }
    }

    private fun highlightNav(active: TextView) {
        val normalColor = resources.getColor(android.R.color.darker_gray)
        val activeColor = resources.getColor(android.R.color.white)

        listOf(navClasificacion, navGenero, navTipo).forEach {
            it.setTextColor(normalColor)
            it.alpha = 0.6f
        }

        active.setTextColor(activeColor)
        active.alpha = 1f
    }

    private fun cargarFiltrosDesdeServidor() {
        val prefs = requireContext().getSharedPreferences("filtros_prefs", Context.MODE_PRIVATE)
        val savedClas = prefs.getString("clasificaciones", "") ?: ""
        val savedGen  = prefs.getString("generos", "") ?: ""
        val savedTipo = prefs.getString("tipos", "") ?: ""

        fun csvContains(csv: String, id: String): Boolean {
            if (csv.isBlank()) return false
            val set = csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            return set.contains(id)
        }

        val url = "http://192.168.1.4/Kinora/kinora_php/obtener_filtros.php"
        val queue = Volley.newRequestQueue(requireContext())

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val json = JSONObject(response)

                    // Clasificaciones
                    val containerClas = view?.findViewById<LinearLayout>(R.id.container_clasificaciones)
                    containerClas?.removeAllViews()

                    val clasArray = json.optJSONArray("clasificaciones")
                    if (clasArray != null) {
                        for (i in 0 until clasArray.length()) {
                            val obj = clasArray.getJSONObject(i)
                            val id = obj.getString("id_clasificacion")
                            val label = obj.getString("clasificacion")

                            val sw = SwitchCompat(requireContext())
                            val lp = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            lp.setMargins(0, 8, 0, 8)
                            sw.layoutParams = lp
                            sw.text = label
                            sw.tag = id
                            sw.isChecked = csvContains(savedClas, id)

                            containerClas?.addView(sw)
                        }
                    }

                    // Generos
                    val chipGroupGeneros = view?.findViewById<ChipGroup>(R.id.chipgroup_generos)
                    chipGroupGeneros?.removeAllViews()

                    val genArray = json.optJSONArray("generos")
                    if (genArray != null) {
                        for (i in 0 until genArray.length()) {
                            val obj = genArray.getJSONObject(i)
                            val id = obj.getString("id_genero")
                            val label = obj.getString("genero")

                            val chip = Chip(requireContext())
                            val lp = ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            lp.setMargins(0, 0, 8, 8)
                            chip.layoutParams = lp
                            chip.text = label
                            chip.isCheckable = true
                            chip.tag = id

                            chip.isChecked = csvContains(savedGen, id)

                            chipGroupGeneros?.addView(chip)
                        }
                    }

                    // Tipos
                    val chipGroupTipos = view?.findViewById<ChipGroup>(R.id.chipgroup_tipos)
                    chipGroupTipos?.removeAllViews()

                    val tipoArray = json.optJSONArray("tipos")
                    if (tipoArray != null) {
                        for (i in 0 until tipoArray.length()) {
                            val obj = tipoArray.getJSONObject(i)
                            val id = obj.getString("id_tipo")
                            val label = obj.getString("tipo")

                            val chip = Chip(requireContext())
                            val lp = ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            lp.setMargins(0, 0, 8, 8)
                            chip.layoutParams = lp
                            chip.text = label
                            chip.isCheckable = true
                            chip.tag = id

                            chip.isChecked = csvContains(savedTipo, id)

                            chipGroupTipos?.addView(chip)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        queue.add(request)
    }

    private fun limpiarSeleccion() {
        val containerClas = view?.findViewById<LinearLayout>(R.id.container_clasificaciones)
        containerClas?.let {
            for (i in 0 until it.childCount) {
                val v = it.getChildAt(i)
                if (v is SwitchCompat) v.isChecked = false
            }
        }
        val cg = view?.findViewById<ChipGroup>(R.id.chipgroup_generos)
        cg?.let {
            for (i in 0 until it.childCount) {
                (it.getChildAt(i) as? Chip)?.isChecked = false
            }
        }
        val ct = view?.findViewById<ChipGroup>(R.id.chipgroup_tipos)
        ct?.let {
            for (i in 0 until it.childCount) {
                (it.getChildAt(i) as? Chip)?.isChecked = false
            }
        }

        val prefs = requireContext().getSharedPreferences("filtros_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("clasificaciones").remove("generos").remove("tipos").apply()
    }


    private fun aplicarFiltrosYEnviarResultado() {
        val selectedClas = mutableListOf<String>()
        val containerClas = view?.findViewById<LinearLayout>(R.id.container_clasificaciones)
        containerClas?.let {
            for (i in 0 until it.childCount) {
                val v = it.getChildAt(i)
                if (v is SwitchCompat && v.isChecked) {
                    selectedClas.add(v.tag.toString())
                }
            }
        }

        val selectedGen = mutableListOf<String>()
        val cg = view?.findViewById<ChipGroup>(R.id.chipgroup_generos)
        cg?.let {
            for (i in 0 until it.childCount) {
                val chip = it.getChildAt(i) as? Chip
                if (chip != null && chip.isChecked) selectedGen.add(chip.tag.toString())
            }
        }

        val selectedTipo = mutableListOf<String>()
        val ct = view?.findViewById<ChipGroup>(R.id.chipgroup_tipos)
        ct?.let {
            for (i in 0 until it.childCount) {
                val chip = it.getChildAt(i) as? Chip
                if (chip != null && chip.isChecked) selectedTipo.add(chip.tag.toString())
            }
        }

        val clasCsv = selectedClas.joinToString(",")
        val genCsv = selectedGen.joinToString(",")
        val tipoCsv = selectedTipo.joinToString(",")

        val prefs = requireContext().getSharedPreferences("filtros_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("clasificaciones", clasCsv)
            .putString("generos", genCsv)
            .putString("tipos", tipoCsv)
            .apply()

        val bundle = bundleOf(
            "clasificaciones" to clasCsv,
            "generos" to genCsv,
            "tipos" to tipoCsv
        )
        parentFragmentManager.setFragmentResult("filtros_aplicados", bundle)

        dismiss()
    }

}


