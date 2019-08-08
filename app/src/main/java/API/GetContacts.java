package API;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import Model.Contact;
import Utils.Constants;
import Utils.Utilz;

public class GetContacts extends AsyncTask<Void, Void, Void> {

    Context context;

    public GetContacts(Context context, OnContactLoad listener) {
        this.context = context;
        this.listener = listener;
        Utilz.showProgressDialog(context);
    }

    OnContactLoad listener;

    public interface OnContactLoad {
        public void successToLoadContacts();
    }

    @Override
    protected Void doInBackground(Void... params) {

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Query phone here. Covered next

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String result = phoneNo.replaceAll("[^\\w\\s+]", "");
                        result = result.replaceAll(" ", "");

                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        String email = null;
                        while (emailCur.moveToNext()) {
                            if (email == null) {
                                email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            }
                        }
                        Contact contact = new Contact();
                        contact.setName(name);
                        if (name != null && !name.isEmpty()) {
                            if (!Constants.phoneNumbers.contains(result)) {
                                contact.setPhoneNo(result);
                                Constants.phoneNumbers.add(result);
                            }
                        }

                        if (email != null && !email.isEmpty() && !Constants.emails.contains(email.toLowerCase())) {
                            Constants.emails.add(email.toLowerCase());
                            contact.setEmail(email.toLowerCase());
                        } else {
                            contact.setEmail("");
                        }

                        if ((contact.getEmail() != null && !contact.getEmail().isEmpty()) || (contact.getPhoneNo() != null && !contact.getPhoneNo().isEmpty())) {
                            String contactName=contact.getName();
//                            if(Constants.ContactNames.contains(contact.getName())){
//                                int phone_type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//
//                                switch (phone_type)
//                                {
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                                        contactName = contactName + " (HOME)";
//                                        break;
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                                        contactName = contactName + " (MOBILE)";
//                                        break;
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                                        contactName = contactName + " (WORK)";
//                                        break;
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//                                        contactName = contactName + " (OTHER)";
//                                        break;
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
//                                        contactName = contactName + " (CUSTOM)";
//                                        break;
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
//                                        contactName = contactName + " (PAGER)";
//                                        break;
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
//                                        contactName = contactName + " (MAIN)";
//                                        break;
//                                }
//                                contact.setName(contactName);
//                            }
                            Constants.ContactNames.add(contactName);
                            Constants.contacts.add(contact);
                        }

                        emailCur.close();
                    }
                    pCur.close();
                }
            }
        }
        cur.close();

        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.e("Name :", name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.e("Email", email);
                    if (email != null) {
                        if (email != null && !email.isEmpty() && !Constants.emails.contains(email.toLowerCase())) {
                            Contact contact = new Contact();
                            contact.setName(name);
                            contact.setPhoneNo("");
                            Constants.emails.add(email.toLowerCase());
                            contact.setEmail(email.toLowerCase());
                            Constants.contacts.add(contact);
                        }

                    }
                }
                cur1.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.i("Contacts ", "Fetched " + Constants.contacts.size());
        listener.successToLoadContacts();
    }
}