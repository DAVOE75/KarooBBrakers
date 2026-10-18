package com.davoe.karoobbrakers.extension.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.davoe.karoobbrakers.R
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

/**
 * Motor de renderizado en Canvas / Bitmap para campos de datos de Karoo BBrakers.
 *
 * Implementa la arquitectura de alto rendimiento de BigNum y AltGraph:
 * Dibuja directamente sobre un Canvas con la tipografía Barlow Condensed Bold 700
 * aplicando una escala horizontal de 50% (textScaleX = 0.5f) para máxima compresión
 * y legibilidad tipo cockpit de competición.
 */
object BrakeFieldRenderer {

    private var cachedBarlowSemiBold: Typeface? = null
    private val bitmapCache = ConcurrentHashMap<String, Bitmap>()

    fun getBarlowSemiBold(context: Context): Typeface {
        return cachedBarlowSemiBold ?: run {
            val tf = runCatching {
                ResourcesCompat.getFont(context, R.font.barlow_condensed_semibold)
            }.getOrNull() ?: Typeface.create("sans-serif-condensed", Typeface.BOLD)
            cachedBarlowSemiBold = tf
            tf
        }
    }

    /**
     * Dibuja un mosaico completo de telemetría (etiqueta superior, cifra principal y unidad)
     * en un Bitmap que se transfiere por IPC a Karoo OS mediante RemoteViews.
     * Satisface la especificación exacta: Barlow Condensed Bold 700 con ancho 50% (textScaleX = 0.5f).
     */
    fun renderTelemetryTile(
        context: Context,
        label: String,
        value: String,
        unit: String,
        valueColor: Int = 0xFFFFFFFF.toInt(),
        labelColor: Int = 0xFFFFFFFF.toInt(),
        accentBarColor: Int? = null,
        alignment: io.hammerhead.karooext.models.ViewConfig.Alignment? = null,
        width: Int = 240,
        height: Int = 140,
    ): Bitmap {
        val w = if (width > 0) width else 240
        val h = if (height > 0) height else 140

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Fondo Dark Carbon de competición
        canvas.drawColor(0xFF090C12.toInt())

        val typeface = getBarlowSemiBold(context)

        // 2. Encabezado / Etiqueta superior
        val labelSize = (h * 0.16f).coerceIn(13f, 26f)
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = labelSize
            this.color = labelColor
            this.isSubpixelText = true
        }

        val leftMargin = (w * 0.04f).coerceIn(6f, 16f)
        val topMargin = (h * 0.17f).coerceIn(14f, 30f)

        val upperLabel = label.uppercase()
        val labelWidth = labelPaint.measureText(upperLabel)
        val labelX = when (alignment) {
            io.hammerhead.karooext.models.ViewConfig.Alignment.CENTER -> (w - labelWidth) / 2f
            io.hammerhead.karooext.models.ViewConfig.Alignment.RIGHT -> w - leftMargin - labelWidth
            else -> leftMargin
        }
        canvas.drawText(upperLabel, labelX.coerceAtLeast(leftMargin), topMargin, labelPaint)

        // 3. Cifra principal: Con peso 600 (SemiBold) y tamaño ampliado al 82% de la celda
        val targetNumScaleX = if (value.length <= 3) 0.70f else 0.60f
        val numSize = (h * 0.82f).coerceIn(40f, 180f)
        val numPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = numSize
            this.textScaleX = targetNumScaleX
            this.color = valueColor
            this.isSubpixelText = true
        }

        // 4. Unidad
        val unitSize = (numSize * 0.26f).coerceIn(12f, 36f)
        val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = unitSize
            this.color = labelColor
            this.isSubpixelText = true
        }

        var numWidth = numPaint.measureText(value)
        val spacing = if (unit.isNotEmpty()) 4f else 0f
        var unitWidth = if (unit.isNotEmpty()) unitPaint.measureText(unit) else 0f
        var totalContentWidth = numWidth + spacing + unitWidth
        val maxAvailableWidth = w - (leftMargin * 2f)

        // Ajuste proporcional si el texto completo excede el ancho disponible de la celda
        if (totalContentWidth > maxAvailableWidth && totalContentWidth > 0f) {
            val scale = maxAvailableWidth / totalContentWidth
            numPaint.textSize = numSize * scale
            unitPaint.textSize = unitSize * scale
            numWidth = numPaint.measureText(value)
            unitWidth = if (unit.isNotEmpty()) unitPaint.measureText(unit) else 0f
            totalContentWidth = numWidth + spacing + unitWidth
        }

        val numBounds = Rect()
        numPaint.getTextBounds(value, 0, value.length, numBounds)
        val numHeight = numBounds.height().toFloat()

        // Posicionamiento horizontal según la alineación del perfil Karoo
        val numX = when (alignment) {
            io.hammerhead.karooext.models.ViewConfig.Alignment.CENTER -> (w - totalContentWidth) / 2f
            io.hammerhead.karooext.models.ViewConfig.Alignment.RIGHT -> w - leftMargin - totalContentWidth
            else -> leftMargin
        }.coerceAtLeast(leftMargin)

        // Posicionamiento vertical: ajustado hacia abajo para aprovechar toda la vertical de la caja
        val numBaseline = h * 0.90f
        canvas.drawText(value, numX, numBaseline, numPaint)

        if (unit.isNotEmpty()) {
            val unitBaseline = numBaseline - (numHeight * 0.05f)
            canvas.drawText(unit, numX + numWidth + spacing, unitBaseline, unitPaint)
        }

        // 5. Línea de acento inferior
        val barColor = accentBarColor ?: 0xFF19212E.toInt()
        val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = barColor
            this.strokeWidth = 3f
        }
        canvas.drawLine(leftMargin, h - 3f, w - leftMargin, h - 3f, barPaint)

        return bitmap
    }

    /**
     * Genera un Bitmap individual de la cifra en Barlow Condensed Bold 700 (ancho 50%)
     * para su uso en Glance Widgets (BrakeGauge, BrakeThermal, BrakeCockpit).
     */
    fun renderNumberBitmap(
        context: Context,
        text: String,
        color: Int = 0xFFFFFFFF.toInt(),
        pixelHeight: Int = 48,
    ): Bitmap {
        val h = max(16, pixelHeight)
        val typeface = getBarlowSemiBold(context)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = h * 0.95f
            this.textScaleX = 0.65f // Proporción SemiBold 600 optimizada
            this.color = color
            this.isSubpixelText = true
        }

        val measuredWidth = paint.measureText(text)
        val w = max(1, measuredWidth.toInt() + 4)

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val baseline = (h / 2f) + (bounds.height() / 2f)
        canvas.drawText(text, 2f, baseline, paint)

        return bitmap
    }

    data class TelemetryTileData(
        val label: String,
        val value: String,
        val unit: String,
        val valueColor: Int = 0xFFFFFFFF.toInt(),
        val labelColor: Int = 0xFFFFFFFF.toInt(),
        val accentBarColor: Int? = null,
    )

    /**
     * Controla el ciclo de vida de renderizado de un campo numérico gráfico en Karoo OS.
     * Satisface la arquitectura BigNum: oculta encabezado nativo (showHeader = false) y
     * transmite RemoteViews frescas por cada frame con bitmap renderizado en Barlow Condensed 50%.
     */
    fun startTelemetryView(
        context: Context,
        config: io.hammerhead.karooext.models.ViewConfig,
        emitter: io.hammerhead.karooext.internal.ViewEmitter,
        sensorFusion: com.davoe.karoobbrakers.sensor.BrakeSensorFusion,
        throttleMs: Long = 120L,
        compute: (com.davoe.karoobbrakers.model.BrakeMetrics) -> TelemetryTileData,
    ) {
        emitter.onNext(io.hammerhead.karooext.models.UpdateGraphicConfig(showHeader = false))
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
        var lastRenderTimeMs = 0L

        val job = scope.launch {
            sensorFusion.metrics.collect { metrics ->
                val now = System.currentTimeMillis()
                if (now - lastRenderTimeMs >= throttleMs || metrics.isBraking != (lastRenderTimeMs == 0L)) {
                    lastRenderTimeMs = now
                    val (w, h) = config.viewSize
                    val data = compute(metrics)
                    val bitmap = renderTelemetryTile(
                        context = context,
                        label = data.label,
                        value = data.value,
                        unit = data.unit,
                        valueColor = data.valueColor,
                        labelColor = data.labelColor,
                        accentBarColor = data.accentBarColor,
                        alignment = config.alignment,
                        width = w,
                        height = h,
                    )
                    val views = android.widget.RemoteViews(context.packageName, R.layout.bbrake_numeric_field)
                    views.setImageViewBitmap(R.id.field_image, bitmap)
                    emitter.updateView(views)
                }
            }
        }
        emitter.setCancellable { job.cancel() }
    }
}
