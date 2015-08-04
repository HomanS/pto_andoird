package nu.pto.androidapp.base.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import nu.pto.androidapp.base.BaseActivity;
import nu.pto.androidapp.base.R;
import nu.pto.androidapp.base.Updateable;
import nu.pto.androidapp.base.db.dao.ClientDao;
import nu.pto.androidapp.base.db.dao.ClientPackageDao;
import nu.pto.androidapp.base.db.model.Client;
import nu.pto.androidapp.base.db.model.ClientPackage;
import nu.pto.androidapp.base.util.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

public class PersonalInfoFragment extends BaseFragment implements Updateable {

    private ImageView pictureImageView;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private EditText yearOfBirthEditText;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_FILE_SELECT = 2;
    private AlertDialog alert;
    private int clientId;
    private Bitmap newPhoto = null;

    public PersonalInfoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.wtf(BaseActivity.LOG_TAG, "PersonalInfo-on View Create");
        clientId = SharedPreferencesHelper.getInt(getActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
        final View rootView = inflater.inflate(R.layout.fragment_personal_info, container, false);

        firstNameEditText = (EditText) rootView.findViewById(R.id.personal_info_first_name);
        lastNameEditText = (EditText) rootView.findViewById(R.id.personal_info_last_name);
        phoneNumberEditText = (EditText) rootView.findViewById(R.id.personal_info_phone_number);
        emailEditText = (EditText) rootView.findViewById(R.id.personal_info_mail);
        pictureImageView = (ImageView) rootView.findViewById(R.id.personal_info_photo);
        yearOfBirthEditText = (EditText) rootView.findViewById(R.id.et_date_of_birth_personal_info);

        yearOfBirthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showNumberPicker();
                }
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.wtf(BaseActivity.LOG_TAG, "PersonalInfo-on Activity Created");
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getActivity().findViewById(R.id.personal_info_change_picture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePhotoPopup();
            }
        });

        ImageView ivClose = (ImageView) getBaseActivity().findViewById(R.id.btn_cancel_custom_action_bar_activity);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ImageView ivSave = (ImageView) getBaseActivity().findViewById(R.id.btn_save_custom_action_bar_activity);

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String dob = yearOfBirthEditText.getText().toString();
                String phone = phoneNumberEditText.getText().toString();
                String mail = emailEditText.getText().toString();

                String errorMessages = "";

                if (!ValidatorUtil.isValidDateOfBirth(dob)) {
                    errorMessages += getActivity().getString(R.string.string_year_of_birth);
                }
                if (!ValidatorUtil.isValidPhoneNumber(phone)) {
                    errorMessages += getActivity().getString(R.string.string_phone_number);
                }
                if (!ValidatorUtil.isValidEmailAddress(mail)) {
                    errorMessages += getActivity().getString(R.string.string_email);
                }

                if (!errorMessages.equals("")) {
                    showInvalidFieldError(errorMessages);
                    return;
                }

                int clientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);

                try {
                    ClientDao clientDao = getBaseActivity().getDatabaseHelper().getClientDao();
                    ClientPackageDao clientPackageDao = getBaseActivity().getDatabaseHelper().getClientPackageDao();

                    Client client = clientDao.getClientByClientId(clientId);
                    ClientPackage clientPackage = clientPackageDao.getClientPackageByClientIdAndPackageId(client.clientId, client.packageId);

                    client.firstName = firstName;
                    client.lastName = lastName;

                    client.email = mail;
                    client.phoneNumber = phone;
                    client.yearOfBirth = Integer.parseInt(dob);

                    client.updatedDate = Utils.getCurrentTimestamp();
                    client.syncStatus = SyncStatus.CHANGED;
                    clientDao.update(client);

                    if(clientPackage != null) {
                        clientPackage.answer1 = Integer.toString(client.yearOfBirth);
                        clientPackageDao.update(clientPackage);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(newPhoto != null){
                    saveBitmap(newPhoto);
                }

                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        Log.wtf(BaseActivity.LOG_TAG, "PersonalInfo-on Resume");
        super.onResume();
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            int clientId = SharedPreferencesHelper.getInt(getBaseActivity(), SharedPreferencesHelper.KEY_CLIENT_ID);
            ClientDao clientDao = getBaseActivity().getDatabaseHelper().getClientDao();
            final Client client = clientDao.getClientByClientId(clientId);

            firstNameEditText.setText(client.firstName);
            lastNameEditText.setText(client.lastName);
            phoneNumberEditText.setText(client.phoneNumber);
            emailEditText.setText(client.email);
            pictureImageView.setTag(client.photo);
            if(newPhoto == null) {
                if(client.photo != null && !client.photo.equals("")) {

                    // load from client.photo, already synced

                    DbImageLoader.getInstance().load(getActivity(), client.photo, pictureImageView);
                }else{

                    // load from client.photo_base64 , not synced yet
                    
                    byte[] decodedString = Base64.decode(client.photoBase64, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    pictureImageView.setImageBitmap(decodedImage);
                }
            }else{

            }
            yearOfBirthEditText.setText(Integer.toString(client.yearOfBirth));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void showInvalidFieldError(String errorMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle(getActivity().getString(R.string.string_invalid_input_fields));
        builder.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(getActivity().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }


    private void showNumberPicker() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(currentYear - 100);
        numberPicker.setMaxValue(currentYear - 6);
        numberPicker.setWrapSelectorWheel(true);

        numberPicker.setValue(Integer.parseInt(yearOfBirthEditText.getText().toString()));
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.dialog_title_year_of_birth));

        alert.setPositiveButton(getActivity().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int yearOfBirth = numberPicker.getValue();
                yearOfBirthEditText.setText(Integer.toString(yearOfBirth));
                emailEditText.requestFocus();
            }
        });

        alert.setNegativeButton(getActivity().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                emailEditText.requestFocus();
            }
        });

        alert.setView(numberPicker);
        alert.show();
    }


    @Override
    public void updateView() {

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.wtf(BaseActivity.LOG_TAG, "PersonalInfo-on Activity Result");
        super.onActivityResult(requestCode, resultCode, data);
        System.gc();

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            File file = new File(Environment.getExternalStorageDirectory(), "pto_profile_pic.jpg");

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), o);

            int size = bitmap.getWidth();
            if(size > bitmap.getHeight()) {
                size = bitmap.getHeight();
            }

            int width = size;
            int x = (bitmap.getWidth()-width)/2;
            if(x < 0) {
                x = 0;
                width = bitmap.getWidth();
            }
            int height = size;
            int y = (bitmap.getHeight()-height)/2;
            if(y < 0) {
                y = 0;
                height = bitmap.getHeight();
            }

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(
                        file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int rotation = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
            }

            Matrix mat = new Matrix();
            mat.postRotate(rotation);

            bitmap = Bitmap.createBitmap(bitmap,x,y,width,height, mat, false);

            ImageView imageView = (ImageView) getActivity().findViewById(R.id.personal_info_photo);

            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

            newPhoto = bitmap;
        }
        if (requestCode == REQUEST_FILE_SELECT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                AssetFileDescriptor fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor( uri, "r");

                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

                int size = bitmap.getWidth();
                if(size > bitmap.getHeight()) {
                    size = bitmap.getHeight();
                }


                int width = size;
                int x = (bitmap.getWidth()-width)/2;
                if(x < 0) {
                    x = 0;
                    width = bitmap.getWidth();
                }
                int height = size;
                int y = (bitmap.getHeight()-height)/2;
                if(y < 0) {
                    y = 0;
                    height = bitmap.getHeight();
                }

                bitmap = Bitmap.createBitmap(bitmap,x,y,width,height);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ImageView imageView = (ImageView) getActivity().findViewById(R.id.personal_info_photo);

            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

            newPhoto = bitmap;
        }

    }

    public void saveBitmap(Bitmap bitmap) {
        ByteArrayOutputStream biByteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, biByteArrayOutputStream);
        byte[] b = biByteArrayOutputStream.toByteArray();
        String pictureToResizeBase64 = Base64.encodeToString(b, Base64.DEFAULT);

        try {
            ClientDao clientDao = getDatabaseHelper().getClientDao();
            Client client = clientDao.getClientByClientId(clientId);
            client.photoBase64 = pictureToResizeBase64;
            client.photo = "";
            clientDao.update(client);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void dispatchTakePictureIntent() {
        alert.dismiss();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory(), "pto_profile_pic.jpg");
        Uri outputFileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);


        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void showChoosePhotoPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.layout_choose_photo, null);
        builder.setView(rootView);
        alert = builder.create();
        alert.show();
        // take photo
        rootView.findViewById(R.id.b_from_camera_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        // choose from library
        rootView.findViewById(R.id.b_from_library_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.string_select_file_to_upload)), REQUEST_FILE_SELECT);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                }
            }
        });
        // cancel
        rootView.findViewById(R.id.b_cancel_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
    }

}
