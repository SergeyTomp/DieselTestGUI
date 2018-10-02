package fi.stardex.sisu.util.enums;

public enum BoschImg {

    CRI10("/img/avan_cri_1.jpg"),
    CRI20("/img/avan_cri_2.jpg"),
    CRI21("/img/avan_cri_21.jpg"),
    CRI22("/img/avan_cri_22.jpg"),
    CRIN1("/img/avan_crin_1.jpg"),
    CRIN2("/img/avan_crin_2.jpg"),
    CRIN3("/img/avan_crin_3.jpg"),
    CRIN2A("/img/avan_crin_2.jpg"),
    CRIN3L("/img/avan_crin_3.jpg");

    String url;

    BoschImg(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
