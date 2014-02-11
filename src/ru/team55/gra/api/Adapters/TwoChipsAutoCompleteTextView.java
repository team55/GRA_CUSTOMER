package ru.team55.gra.api.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import ru.team55.gra.api.pocoObjects.pocoSearchItem;
import ru.team55.gra.rating.R;

import java.util.LinkedList;
import java.util.List;


//TODO: основные задачи - убрать запятые, сделать список объектов, добавить свойства от модели.

public class TwoChipsAutoCompleteTextView extends MultiAutoCompleteTextView implements OnItemClickListener {

    public interface TwoChipsItemsChangedListener {
        void Updated(int count);
        void AddedItem(pocoSearchItem item);
    }

    private static final String TAG = TwoChipsAutoCompleteTextView.class.getSimpleName();

    public TwoChipsAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }
    public TwoChipsAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public TwoChipsAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    public void init(Context context){
        setOnItemClickListener(this);
        addTextChangedListener(textWather);
    }

    TwoChipsItemsChangedListener listener = null;
    public void setTwoChipsItemsChangedListener(TwoChipsItemsChangedListener listener){
        this.listener = listener;
    }


    //------------------------------------------------------------------------------------------------------------------
    private TextWatcher textWather = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count >=1){
                if(s.charAt(start) == ',')
                    rebuildChips(true); // generate chips
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {

        pocoSearchItem item = (pocoSearchItem)getAdapter().getItem(position);
        items.add(item);
        if(listener!=null) listener.AddedItem(item);

        rebuildChips(true); // call generate chips when user select any item from auto complete

        if(items.size()==2){
            setCursorVisible(false);
            setEnabled(false);
        }
    }


    public void deleteLast(){
        if(items.size()>0)  {
            items.remove(items.get(items.size()-1));
            setCursorVisible(true);
            setEnabled(true);
        }
        rebuildChips(true);
    }

    void rebuildText(){

        StringBuilder sb = new StringBuilder();
        for(pocoSearchItem si : items){
            sb.append(si.name);
            sb.append(", ");
        }
        setText(sb.toString());
    }



    List<pocoSearchItem> items = new LinkedList<pocoSearchItem>();

    public void ReplaceValues(List<pocoSearchItem> newitems){
       items = newitems;
       rebuildChips(false);
    }

    public List<pocoSearchItem> getItems(){
        return items;
    }


    //--------------------------------ЯЯЯЯЯЯЯ

    //This function has whole logic for chips generate
    public void rebuildChips(boolean doCallback){


        if(listener!=null && doCallback) listener.Updated(items.size());

        rebuildText();

        // check comman in string
        //if(getText().toString().contains(",")) {
        if(items.size()>0) {

            SpannableStringBuilder ssb = new SpannableStringBuilder(getText()); //текущий текст для разбивки по которому пробегаемся



            // split string wich comma
            //String chips[] = getText().toString().trim().split(",");

            //Тут будут типы объектов (адрес или другое)
            //перебираем объекты и добавляем


            int x = 0;


            // loop will generate ImageSpan for every country name separated by comma
            //for(String c : chips){
            for(pocoSearchItem si : items){

                Log.w(TAG, "item="+si.name);

                String c = si.name;

                // inflate chips_edittext layout
                LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                View v =  lf.inflate(R.layout.chips_edittext, null);   //шаблон - в зависимости от типа объекта разные иконки
                TextView tv = (TextView)v.findViewById(R.id.edtTxt1);

                int bubbleSize = getResources().getInteger(R.integer.bubbleSize);
                Log.w("BUBBLE", "size="+ bubbleSize);
                //todo: определение ширины экрана и в зависимости от него
                //tv.setText(c.length()> bubbleSize ?c.substring(0,bubbleSize)+"..  ":c); // set text

                tv.setText(c);

                //setFlags(textView, si); //TODO: кнопка очистки -  set flag image или иконка вида деятельности или адреса


                // capture bitmapt of genreated textview

                int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                v.measure(spec, spec);
                v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());



                Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                canvas.translate(-v.getScrollX(), -v.getScrollY());
                v.draw(canvas);
                v.setDrawingCacheEnabled(true);
                Bitmap cacheBmp = v.getDrawingCache();
                Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
                v.destroyDrawingCache();  // destory drawable

                // create bitmap drawable for imagespan
                BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
                bmpDrawable.setBounds(0, 0,bmpDrawable.getIntrinsicWidth(),bmpDrawable.getIntrinsicHeight());

                //todo: удаления символов приводит к ошибке
                // create and set imagespan
                ssb.setSpan(new ImageSpan(bmpDrawable),x ,x + c.length()+2 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                //x = x+ c.length() +1;
                x = x+ c.length()+2; //запятая и пробел добавленные токенайзером
            }

            Log.w(TAG, "text="+ssb.toString());

            // set chips span
            setText(ssb);

            // move cursor to last
            setSelection(getText().length());
        }


    }



    // this method set country flag image in textview's drawable component, this logic is not optimize, you need to change as per your requirement
//    public void setFlags(TextView textView,pocoSearchItem itm){
//
//        if(itm.type.equalsIgnoreCase("адрес")){
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_place, 0);
//        }
//        else {
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_tag, 0);
//        }
//
//
//    }
//


    //выделение и генерация выделенных и нет чипсов + очистка кнопкой выделенных




    //--------------------------------ЯЯЯЯЯЯЯ


    /**
     * We cannot use the default mechanism for replaceText. Instead,
     * we override onItemClickListener so we can get all the associated
     * contact information including display text, address, and id.
     */
/*
    @Override
    protected void replaceText(CharSequence text) {
        return;
    }
*/



    /*@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_TAB:
                if (event.hasNoModifiers()) {
                    if (mSelectedChip != null) {
                        clearSelectedChip();
                    } else {
                        commitDefault();
                    }
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

        @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mSelectedChip != null && keyCode == KeyEvent.KEYCODE_DEL) {
            if (mAlternatesPopup != null && mAlternatesPopup.isShowing()) {
                mAlternatesPopup.dismiss();
            }
            removeChip(mSelectedChip);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (event.hasNoModifiers()) {
                    if (commitDefault()) {
                        return true;
                    }
                    if (mSelectedChip != null) {
                        clearSelectedChip();
                        return true;
                    } else if (focusNext()) {
                        return true;
                    }
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }



    rivate void clearSelectedChip() {
        if (mSelectedChip != null) {
            unselectChip(mSelectedChip);
            mSelectedChip = null;
        }
        setCursorVisible(true);
    }

    @Override
    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);
        if (!hasFocus) {
            // shrink(); урезать
        } else {
            //expand(); расширить
        }
    }

    */





    /**
     * Я - удалить чипс при бакспейсе
     */
/*
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mSelectedChip != null) {
            clearSelectedChip();
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }
*/


/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isFocused()) {
            // Ignore any chip taps until this view is focused.
            return super.onTouchEvent(event);
        }
        boolean handled = super.onTouchEvent(event);
        int action = event.getAction();
        boolean chipWasSelected = false;
        if (mSelectedChip == null) {
            mGestureDetector.onTouchEvent(event);
        }
        if (mCopyAddress == null && action == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            int offset = putOffsetInRange(getOffsetForPosition(x, y));
            DrawableRecipientChip currentChip = findChip(offset);
            if (currentChip != null) {
                if (action == MotionEvent.ACTION_UP) {
                    if (mSelectedChip != null && mSelectedChip != currentChip) {
                        clearSelectedChip();
                        mSelectedChip = selectChip(currentChip);
                    } else if (mSelectedChip == null) {
                        setSelection(getText().length());
                        commitDefault();
                        mSelectedChip = selectChip(currentChip);
                    } else {
                        onClick(mSelectedChip, offset, x, y);
                    }
                }
                chipWasSelected = true;
                handled = true;
            } else if (mSelectedChip != null && shouldShowEditableText(mSelectedChip)) {
                chipWasSelected = true;
            }
        }
        if (action == MotionEvent.ACTION_UP && !chipWasSelected) {
            clearSelectedChip();
        }
        return handled;
    }



        private DrawableRecipientChip findChip(int offset) {
        DrawableRecipientChip[] chips =
                getSpannable().getSpans(0, getText().length(), DrawableRecipientChip.class);
        // Find the chip that contains this offset.
        for (int i = 0; i < chips.length; i++) {
            DrawableRecipientChip chip = chips[i];
            int start = getChipStart(chip);
            int end = getChipEnd(chip);
            if (offset >= start && offset <= end) {
                return chip;
            }
        }
        return null;
    }
*/





}
