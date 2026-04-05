package com.openminds.app.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import com.openminds.app.database.entity.FormationTop;
import com.openminds.app.database.entity.StatThematique;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfExportHelper {


    public static class StatsSnapshot {
        public int nbFormations;
        public int nbBenevoles;
        public int nbSessions;
        public int tauxReussite;
        public String labelPeriode;
        public List<StatThematique> thematiques;
        public List<FormationTop> topFormations;
    }

    public static void exporterEtPartager(Context context, StatsSnapshot stats) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        dessinerPdf(canvas, stats);

        document.finishPage(page);


        File fichier = new File(context.getExternalFilesDir(null),
                "rapport_openminds.pdf");
        try {
            document.writeTo(new FileOutputStream(fichier));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            document.close();
        }


        Uri uri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                fichier);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Ouvrir le rapport PDF"));
    }

    private static void dessinerPdf(Canvas canvas, StatsSnapshot s) {
        Paint titreP  = new Paint();
        Paint corpsP  = new Paint();
        Paint kpiP    = new Paint();
        Paint lineP   = new Paint();

        titreP.setColor(Color.parseColor("#1A2F4A"));
        titreP.setTextSize(22f);
        titreP.setFakeBoldText(true);

        corpsP.setColor(Color.parseColor("#333333"));
        corpsP.setTextSize(13f);

        kpiP.setColor(Color.parseColor("#2C4A8A"));
        kpiP.setTextSize(18f);
        kpiP.setFakeBoldText(true);

        lineP.setColor(Color.parseColor("#CCCCCC"));
        lineP.setStrokeWidth(1f);

        int x = 40;
        int y = 60;


        canvas.drawText("OpenMinds — Rapport Statistiques", x, y, titreP);
        y += 20;
        canvas.drawLine(x, y, 555, y, lineP);
        y += 20;

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                .format(new Date());
        canvas.drawText("Généré le " + date + "  |  " + s.labelPeriode, x, y, corpsP);
        y += 30;
        canvas.drawLine(x, y, 555, y, lineP);
        y += 30;


        canvas.drawText("Indicateurs clés", x, y, titreP);
        y += 25;

        dessinerKpi(canvas, x,      y, "Formations",        String.valueOf(s.nbFormations),  kpiP, corpsP);
        dessinerKpi(canvas, x + 130, y, "Bénévoles actifs", String.valueOf(s.nbBenevoles),   kpiP, corpsP);
        dessinerKpi(canvas, x + 270, y, "Taux de réussite", s.tauxReussite + "%",            kpiP, corpsP);
        dessinerKpi(canvas, x + 400, y, "Sessions",         String.valueOf(s.nbSessions),    kpiP, corpsP);
        y += 60;
        canvas.drawLine(x, y, 555, y, lineP);
        y += 25;


        canvas.drawText("Participation par thématique", x, y, titreP);
        y += 25;

        if (s.thematiques != null) {
            int maxInscrits = 0;
            for (StatThematique t : s.thematiques)
                if (t.getNbInscrits() > maxInscrits) maxInscrits = t.getNbInscrits();

            for (StatThematique t : s.thematiques) {
                int pct = maxInscrits > 0 ? (t.getNbInscrits() * 100 / maxInscrits) : 0;
                canvas.drawText(t.getThematique(), x, y, corpsP);


                Paint barBg = new Paint();
                barBg.setColor(Color.parseColor("#E8EDF5"));
                canvas.drawRect(x + 120, y - 12, x + 400, y, barBg);

                Paint barFg = new Paint();
                barFg.setColor(Color.parseColor("#2C4A8A"));
                canvas.drawRect(x + 120, y - 12, x + 120 + (280 * pct / 100), y, barFg);

                canvas.drawText(pct + "%", x + 410, y, corpsP);
                y += 22;
            }
        }
        y += 10;
        canvas.drawLine(x, y, 555, y, lineP);
        y += 25;


        canvas.drawText("Formations les plus suivies", x, y, titreP);
        y += 25;

        if (s.topFormations != null) {
            int rang = 1;
            for (FormationTop f : s.topFormations) {
                canvas.drawText(rang + ".  " + f.getTitre(), x, y, corpsP);
                canvas.drawText(f.getNbInscrits() + " inscrits", 430, y, corpsP);
                y += 20;
                rang++;
            }
        }

        y += 20;
        canvas.drawLine(x, y, 555, y, lineP);
        y += 20;
        corpsP.setColor(Color.parseColor("#888888"));
        corpsP.setTextSize(10f);
        canvas.drawText("Document généré automatiquement par OpenMinds", x, y, corpsP);
    }

    private static void dessinerKpi(Canvas canvas, int x, int y,
                                    String label, String valeur,
                                    Paint kpiP, Paint corpsP) {
        canvas.drawText(valeur, x, y + 20, kpiP);
        canvas.drawText(label,  x, y + 35, corpsP);
    }


    public static void exportAttestationPdf(Context context, String nomPrenom, String nomFormation, String dateObtention) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();


        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        paint.setColor(Color.parseColor("#1ECFB8"));
        canvas.drawRect(30, 30, pageInfo.getPageWidth() - 30, pageInfo.getPageHeight() - 30, paint);


        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(Color.parseColor("#0B1629"));
        paint.setTextSize(36f);
        canvas.drawText("ATTESTATION DE RÉUSSITE", pageInfo.getPageWidth() / 2f, 150, paint);


        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(18f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Le réseau OpenMinds a l'honneur de décerner ce certificat à :", pageInfo.getPageWidth() / 2f, 250, paint);


        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        paint.setTextSize(32f);
        paint.setColor(Color.parseColor("#60A5FA"));
        canvas.drawText(nomPrenom, pageInfo.getPageWidth() / 2f, 320, paint);


        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(18f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Pour avoir complété avec succès le parcours de formation :", pageInfo.getPageWidth() / 2f, 420, paint);


        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(28f);
        paint.setColor(Color.parseColor("#1ECFB8"));
        canvas.drawText(nomFormation, pageInfo.getPageWidth() / 2f, 480, paint);


        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(14f);
        paint.setColor(Color.DKGRAY);
        canvas.drawText("Fait le : " + dateObtention, pageInfo.getPageWidth() / 2f, 650, paint);
        canvas.drawText("L'équipe Pédagogique OpenMinds", pageInfo.getPageWidth() / 2f, 680, paint);

        pdfDocument.finishPage(page);


        String fileName = "Attestation_" + nomFormation.replaceAll("\\s+", "_") + ".pdf";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");

                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri != null) {
                    OutputStream outputStream = resolver.openOutputStream(uri);
                    pdfDocument.writeTo(outputStream);
                    outputStream.close();
                    Toast.makeText(context, "Attestation enregistrée dans Téléchargements !", Toast.LENGTH_LONG).show();
                }
            } else {

                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                pdfDocument.writeTo(fos);
                fos.close();
                Toast.makeText(context, "Attestation enregistrée dans Téléchargements !", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur PDF : " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }
}