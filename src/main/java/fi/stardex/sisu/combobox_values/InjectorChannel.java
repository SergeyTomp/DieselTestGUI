package fi.stardex.sisu.combobox_values;

public enum InjectorChannel {
    SINGLE_CHANNEL("main.channelQty.SINGLE_CHANNEL"), MULTI_CHANNEL("main.channelQty.MULTY_CHANNEL");
    String CHANNEL_QTY;
    InjectorChannel(String channelQty){
        CHANNEL_QTY = channelQty;
    }

    public String getCHANNEL_QTY() {
        return CHANNEL_QTY;
    }
}
