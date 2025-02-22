package is.hbv601g.motorsale.DTOs;

import android.util.Base64;

public class ListingImagesDTO {

    private String imagebase64;
    private Long listingId;
    private byte[] image;

    public ListingImagesDTO(Long listingId, byte[] image) {
        this.listingId = listingId;
        this.image = image;
        if (image != null) {
            this.imagebase64 = image != null ? Base64.encodeToString(image, Base64.DEFAULT) : null;
        }
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
        if (image != null) {
            this.imagebase64 = image != null ? Base64.encodeToString(image, Base64.DEFAULT) : null;
        }
    }

    public String getImageBase64() {
        return imagebase64;
    }
}
