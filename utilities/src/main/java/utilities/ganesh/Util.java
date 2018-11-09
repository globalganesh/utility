package utilities.ganesh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
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
        Log.e("UTIL:", spinner.getCount() +"");
        for (int i = 0; i < spinner.getCount(); i++) {
            Log.e("UTIL", i +"");
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
}
