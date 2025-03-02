package is.hbv601g.motorsale.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import is.hbv601g.motorsale.DTOs.UserDTO;


public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserDTO> userLiveData = new MutableLiveData<>();

    public void setUser(UserDTO user) {
        userLiveData.setValue(user);
    }

    public LiveData<UserDTO> getUser() {
        return userLiveData;
    }
}
