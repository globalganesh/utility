package utilities.ganesh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ganesh on 10/01/17.
 */

public class Util {

    public static final String[] localeId = {"en", "es", "ja", "ko", "pt"};

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void showAlertDialog(final Activity context, String title, String message, int whichButton, final boolean closeScreen) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(whichButton, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (closeScreen)
                    context.finish();
            }
        });
        alertDialog.show();
    }

    public static ProgressDialog showProgress(Activity context) {
        if (!isNetWorkAvailable(context)) {
            showAlertDialog(context, "Warning !", "Please check your internet connectivity...", -2, false);
            return null;
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);

        return dialog;
    }

    public static ProgressDialog showProgress(Activity context, ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing())
            return progressDialog;
        if (!isNetWorkAvailable(context)) {
            showAlertDialog(context, "Warning !", "Please check your internet connectivity...", -2, false);
            return null;
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);

        return dialog;
    }

    public static boolean emailValidator(Context context, String email, String errMsg) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        boolean isValid = matcher.matches();
        if (!isValid)
            showToast(context, errMsg);
        return isValid;
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    public static void setLanguage(Context context, int langId) {
        Locale locale = new Locale(Util.localeId[langId]);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static int getIndex(Spinner spinner, String myString) {
        Log.e("UTIL: ", myString);
        Log.e("UTIL:", spinner.getCount() + "");
        for (int i = 0; i < spinner.getCount(); i++) {
            Log.e("UTIL", i + "");
            Log.e("UTIL: ", spinner.getItemAtPosition(i).toString());
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    public static String formatDate(String inputDate) {
        if (TextUtils.isEmpty(inputDate))
            return "";
        String formattedDate = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date date = inputFormat.parse(inputDate);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = inputFormat.parse(inputDate);
                formattedDate = outputFormat.format(date);
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
        return formattedDate;
    }

    public static boolean isNetworkCheckAlert(Activity activity) {
        if (!isNetWorkAvailable(activity)) {
            showAlertDialog(activity, "Warning !", "Please check your internet connectivity...", -2, true);
            return false;
        }
        return true;
    }

    public static boolean passwordLength(Context context, String password, int length, String errMsg) {
        if (password.length() >= length)
            return true;
        showToast(context, errMsg);
        return false;
    }

    public static void showLongToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static String generateOTP() {
        long timeSeed = System.nanoTime();
        double randSeed = Math.random() * 1000;
        long midSeed = (long) (timeSeed * randSeed);
        String rand6No = midSeed + "";
        return rand6No.substring(0, 6);
    }

    public static boolean isSMSpermissionGrantedAlready(Activity activity, String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap imageOreintationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static void showAlertDialogNoButtons(final Activity activity, String message) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.textview_scroll_alert, null);
        TextView textview = (TextView) view.findViewById(R.id.textmsg);
        textview.setText(message);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();
    }


    public static Double string2double(String input) {
        if (TextUtils.isEmpty(input))
            return 0.0;
        return Double.parseDouble(input);
    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {

            byte[] bytes = new byte[buffer_size];
            for (; ; ) {

                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;

                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}
