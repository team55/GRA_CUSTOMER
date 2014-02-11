package ru.team55.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import ru.team55.gra.api.model;

public class SMSReceiver extends BroadcastReceiver {
    private final static String TAG = SMSReceiver.class.getSimpleName();
    public final static String MESSAGE_NEW_SMS = "ru.team55.messages.NEW_SMS";

//    android.provider.Telephony.SMS_RECEIVED
//    but it doesnt exist in android 2.2


    @Override
    public void onReceive(Context context, Intent intent)
    {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                Log.w("SMS", msgs[i].getMessageBody());

                String from = msgs[i].getOriginatingAddress();

                try {

                    if (from.equals("Agent"+ model.currentRegionId))
                    {
                        String[] s1 = msgs[i].getMessageBody().split(" ");

                        int code = 0;
                        try{
                            code = Integer.parseInt(s1[0]);
                        }
                        catch (Exception ex) {}

                        if(code>0)
                        {
                            Intent intent2 = new Intent(MESSAGE_NEW_SMS);
                            intent2.putExtra("code", code);
                            context.sendBroadcast(intent2);
                            break;
                        }


                    }
                } catch (Exception ex) {
                }


        }
    }


}

}