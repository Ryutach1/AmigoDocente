package cl.ryodantech.amigodocente.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cl.ryodantech.amigodocente.models.ConfiguracionUTP;
import cl.ryodantech.amigodocente.models.Evaluacion;
import cl.ryodantech.amigodocente.models.ItemTabla;
import cl.ryodantech.amigodocente.models.TablaNotas;

public class PdfGeneratorService {

    public static void exportarYCompartir(Context context, Evaluacion evaluacion, TablaNotas tabla, ConfiguracionUTP config) throws IOException {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();

        // Configuración de la página (A4: 595 x 842 puntos)
        int pageWidth = 595;
        int pageHeight = 842;
        int margin = 40;
        int rowHeight = 22; // Un poco más alto para que respire el texto
        int colWidth = (pageWidth - (margin * 2)) / 4; // Ancho para 4 columnas

        int y = 0;
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // --- DIBUJAR MARCA DE AGUA ---
        //dibujarMarcaDeAgua(canvas, pageWidth, pageHeight);

        // --- CABECERA DEL DOCUMENTO ---
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize(18);
        canvas.drawText("Tabla de Notas | " + evaluacion.getNombre(), margin, 50, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        canvas.drawText("Puntaje Máximo: " + evaluacion.getPuntajeMaximo(), margin, 75, paint);
        canvas.drawText("Exigencia: " + (int)(config.getExigencia() * 100) + "%", margin, 95, paint);

        // --- ENCABEZADOS DE LA TABLA (Fondo Gris) ---
        y = 120;
        paint.setColor(Color.parseColor("#E0E0E0")); // Gris claro
        canvas.drawRect(margin, y - 15, pageWidth - margin, y + 5, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setFakeBoldText(true);

        // Coordenadas X para centrar los textos de los encabezados
        float xPuntajeIzq = margin + (colWidth / 2f);
        float xNotaIzq = margin + colWidth + (colWidth / 2f);
        float xPuntajeDer = margin + (colWidth * 2) + (colWidth / 2f);
        float xNotaDer = margin + (colWidth * 3) + (colWidth / 2f);

        canvas.drawText("Puntaje", xPuntajeIzq - 20, y, paint);
        canvas.drawText("Nota", xNotaIzq - 10, y, paint);
        canvas.drawText("Puntaje", xPuntajeDer - 20, y, paint);
        canvas.drawText("Nota", xNotaDer - 10, y, paint);

        y += 20; // Bajamos para empezar a dibujar los datos

        List<ItemTabla> items = tabla.getItems();
        int middle = (items.size() + 1) / 2;

        for (int i = 0; i < middle; i++) {
            // Si nos pasamos del límite inferior de la hoja, creamos una nueva
            if (y > pageHeight - margin - 20) {
                document.finishPage(page);
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                //dibujarMarcaDeAgua(canvas, pageWidth, pageHeight);

                // Repetir encabezados en la nueva hoja
                paint.setColor(Color.parseColor("#E0E0E0"));
                canvas.drawRect(margin, margin - 10, pageWidth - margin, margin + 10, paint);
                paint.setColor(Color.BLACK);
                canvas.drawText("Puntaje", xPuntajeIzq - 20, margin + 5, paint);
                canvas.drawText("Nota", xNotaIzq - 10, margin + 5, paint);
                canvas.drawText("Puntaje", xPuntajeDer - 20, margin + 5, paint);
                canvas.drawText("Nota", xNotaDer - 10, margin + 5, paint);

                y = margin + 30;
            }

            // EFECTO CEBRA (Fila de fondo alterno)
            if (i % 2 == 0) {
                paint.setColor(Color.parseColor("#F5F5F5")); // Gris muy suave
                canvas.drawRect(margin, y - 15, pageWidth - margin, y + 5, paint);
            }

            // --- DATOS IZQUIERDA ---
            ItemTabla itemIzq = items.get(i);
            paint.setFakeBoldText(false);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf((int)itemIzq.getPuntaje()), xPuntajeIzq - 5, y, paint);

            paint.setFakeBoldText(true);
            paint.setColor(itemIzq.getNota() < 4.0 ? Color.RED : Color.BLACK);
            canvas.drawText(String.valueOf(itemIzq.getNota()), xNotaIzq - 5, y, paint);

            // --- DATOS DERECHA ---
            int indexDer = i + middle;
            if (indexDer < items.size()) {
                ItemTabla itemDer = items.get(indexDer);
                paint.setFakeBoldText(false);
                paint.setColor(Color.BLACK);
                canvas.drawText(String.valueOf((int)itemDer.getPuntaje()), xPuntajeDer - 5, y, paint);

                paint.setFakeBoldText(true);
                paint.setColor(itemDer.getNota() < 4.0 ? Color.RED : Color.BLACK);
                canvas.drawText(String.valueOf(itemDer.getNota()), xNotaDer - 5, y, paint);
            }

            y += rowHeight;
        }

        paint.setColor(Color.LTGRAY);
        paint.setTextSize(10);
        canvas.drawText("AmigoDocente", pageWidth / 2 + 180, pageHeight - 20, paint);

        document.finishPage(page);

        // Guardar archivo
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "tabla_" + evaluacion.getNombre().replace(" ", "_") + ".pdf");
        document.writeTo(new FileOutputStream(file));
        document.close();

        // Compartir
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Compartir Tabla de Notas"));
    }
 //linea de codigo del pie de pagina = canvas.drawText("AmigoDocente", pageWidth / 2 + 120, pageHeight - 20, paint);

    /* Metodo para la marca de agua
    private static void dibujarMarcaDeAgua(Canvas canvas, int pageWidth, int pageHeight) {
        Paint watermarkPaint = new Paint();
        watermarkPaint.setColor(Color.argb(30, 0, 0, 0)); // Negro con 12% de opacidad (Gris muy claro)
        watermarkPaint.setTextSize(80); // Letra gigante
        watermarkPaint.setFakeBoldText(true);
        watermarkPaint.setAntiAlias(true);

        // Guardamos el estado del canvas antes de rotar
        canvas.save();

        // Movemos el origen al centro de la hoja y rotamos 45 grados
        canvas.rotate(-45, pageWidth / 2f, pageHeight / 2f);

        // Calculamos el ancho del texto para centrarlo
        String texto = "AmigoDocente";
        float textWidth = watermarkPaint.measureText(texto);

        // Dibujamos en el centro
        canvas.drawText(texto, (pageWidth / 2f) - (textWidth / 2f), (pageHeight / 2f) + 30, watermarkPaint);

        // Restauramos el canvas para que la tabla no quede rotada
        canvas.restore();
    }*/
}