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

        int pageWidth = 595;
        int pageHeight = 842;
        int margin = 40;
        int rowHeight = 20;

        int y = 0;
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // --- DIBUJAR MARCA DE AGUA ---
        //dibujarMarcaDeAgua(canvas, pageWidth, pageHeight);

        // --- CABECERA ---
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize(18);
        canvas.drawText("Tabla de Notas | " + evaluacion.getNombre(), margin, 50, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        canvas.drawText("Puntaje Máximo: " + evaluacion.getPuntajeMaximo(), margin, 75, paint);
        canvas.drawText("Exigencia establecida: " + (int)(config.getExigencia() * 100) + "%", margin, 95, paint);

        y = 130;

        List<ItemTabla> items = tabla.getItems();
        int middle = (items.size() + 1) / 2;

        for (int i = 0; i < middle; i++) {
            if (y > pageHeight - margin - 20) {
                document.finishPage(page);
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();

                // Si hay más de una página, dibujamos la marca de agua también en la nueva
                //dibujarMarcaDeAgua(canvas, pageWidth, pageHeight);

                y = margin + 20;
            }

            ItemTabla itemIzq = items.get(i);
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);
            canvas.drawText((int)itemIzq.getPuntaje() + " pts", margin, y, paint);

            paint.setColor(itemIzq.getNota() < 4.0 ? Color.RED : Color.BLACK);
            canvas.drawText("Nota: " + itemIzq.getNota(), margin + 70, y, paint);

            int indexDer = i + middle;
            if (indexDer < items.size()) {
                ItemTabla itemDer = items.get(indexDer);
                paint.setColor(Color.BLACK);
                canvas.drawText((int)itemDer.getPuntaje() + " pts", pageWidth / 2 + 10, y, paint);

                paint.setColor(itemDer.getNota() < 4.0 ? Color.RED : Color.BLACK);
                canvas.drawText("Nota: " + itemDer.getNota(), pageWidth / 2 + 80, y, paint);
            }

            y += rowHeight;
        }

        // Pie de página de la última hoja
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(10);
        canvas.drawText("AmigoDocente", pageWidth / 2 + 120, pageHeight - 20, paint);

        document.finishPage(page);

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "tabla_" + evaluacion.getNombre().replace(" ", "_") + ".pdf");
        document.writeTo(new FileOutputStream(file));
        document.close();

        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Compartir Tabla de Notas"));
    }

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