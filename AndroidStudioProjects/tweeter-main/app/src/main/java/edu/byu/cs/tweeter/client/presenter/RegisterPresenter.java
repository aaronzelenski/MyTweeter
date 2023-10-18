package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver{

    private final View view;

    public interface View {
        void showErrorMessage(String message);
        void hideErrorMessage();
        void showInfoMessage(String message);
        void hideInfoMessage();
        void openMainView(User user);
    }


    public RegisterPresenter(View view) {
        this.view = view;
    }

    public boolean validateRegistration(String firstName, String lastName, String alias, String password, Drawable image) {
        if (firstName.isEmpty() || lastName.isEmpty() || alias.isEmpty() || password.isEmpty()) {
            view.showErrorMessage("Please fill out all fields.");
            return false;
        }
        if (!alias.startsWith("@") || alias.length() < 2) {
            view.showErrorMessage("Invalid Alias format.");
            return false;
        }
        if (image == null) {
            view.showErrorMessage("Profile image must be uploaded.");
            return false;
        }
        return true;
    }



    public void register(String firstName, String lastName, String alias, String password, Drawable image){
        if(validateRegistration(firstName, lastName, alias, password, image)){

            view.hideErrorMessage();
            view.showInfoMessage("Registering...");


            // put this code into the view...
            Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);


            var userService = new UserService();
            userService.register(firstName, lastName, alias, password, imageBytesBase64,this);
        }
    }

    @Override
    public void registerSucceeded(User user, AuthToken authToken) {
        view.hideErrorMessage();
        view.hideInfoMessage();
        view.showInfoMessage("Registering successful. Hello, " + user.getName() + "!");
        view.openMainView(user);
    }

    @Override
    public void registerSucceeded(User user) {
        view.openMainView(user);
    }

    @Override
    public void registerFailed(String message) {
        view.showErrorMessage(message);
    }

    @Override
    public void handleException(Exception exception) {
    }


}
