package is.hbv601g.motorsale.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import is.hbv601g.motorsale.DTOs.ListingDTO;
import is.hbv601g.motorsale.DTOs.UserDTO;


public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserDTO> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<ListingDTO>> allListings = new MutableLiveData<>();
    private final MutableLiveData<List<ListingDTO>> filteredListings = new MutableLiveData<>();

    public void setUser(UserDTO user) {
        userLiveData.setValue(user);
    }

    public LiveData<UserDTO> getUser() {
        return userLiveData;
    }

    public LiveData<List<ListingDTO>> getAllListings() {
        return allListings;
    }

    public LiveData<List<ListingDTO>> getFilteredListings() {
        return filteredListings;
    }
    public void setAllListings(List<ListingDTO> Listings) {
        allListings.setValue(Listings);
    }

    public void setFilteredListings(List<ListingDTO> listings) {
        filteredListings.setValue(listings);
    }
}
