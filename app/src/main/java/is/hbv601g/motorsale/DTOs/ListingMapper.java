package is.hbv601g.motorsale.DTOs;

import is.hbv601g.motorsale.entities.Listing;

import java.util.ArrayList;
import java.util.List;

public class ListingMapper {

    public static ListingDTO toListingDTO(Listing listing) {
        ListingDTO listingDTO = new ListingDTO(listing.getListingId(), listing.getMotorVehicle(), listing.getPrice(), listing.getAddress(), listing.getPostalCode(), listing.getCity(), listing.getDescription(), listing.getUserId(), listing.getImage());
        return listingDTO;
    }

    public static List<ListingDTO> toListingsDTO(List<Listing> listings) {
        List<ListingDTO> listingsDTO = new ArrayList<>();
        for (Listing listing : listings) {
            listingsDTO.add(toListingDTO(listing));
        }
        return listingsDTO;
    }

}
