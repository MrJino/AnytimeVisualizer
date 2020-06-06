package noh.jinil.app.anytime.helper;


/**
 * @author Paul Burke (ipaulpro)
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();


    /**
     * state should be cleared.
     */
    void onItemClear();

    void onItemMoved(int index);
}