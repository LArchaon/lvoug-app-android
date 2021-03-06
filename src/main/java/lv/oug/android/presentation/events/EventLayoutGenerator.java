package lv.oug.android.presentation.events;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.j256.ormlite.dao.ForeignCollection;
import lv.oug.android.domain.Contact;
import lv.oug.android.domain.Sponsor;
import lv.oug.android.presentation.common.imageloader.ImageLoader;

import javax.inject.Inject;

public class EventLayoutGenerator {

    public static final int CONTACTS_IN_ROW = 2;
    public static final int SPONSORS_IN_ROW = 3;

    @Inject
    Context context;

    @Inject
    ImageLoader imageLoader;

    public void generateSponsorsLayout(LinearLayout sponsorsContainer, ForeignCollection<Sponsor> sponsors) {
        int sponsorsCount = sponsors.size();
        if (sponsorsCount > 0) {
            sponsorsContainer.setVisibility(View.VISIBLE);
        }

        LinearLayout sponsorsRow = null;
        int lastSponsorsRowWeightSum = sponsorsCount % SPONSORS_IN_ROW;
        int rowCount = (sponsorsCount + SPONSORS_IN_ROW - 1) / SPONSORS_IN_ROW;
        int sponsorsRowsAdded = 0;
        int sponsorsAddedInRow = 0;

        for (Sponsor sponsor : sponsors) {
            if (sponsorsAddedInRow == 0 || sponsorsAddedInRow == SPONSORS_IN_ROW) {
                sponsorsAddedInRow = 0;
                sponsorsRowsAdded++;
                sponsorsRow = getSponsorsRowLinearLayout(lastSponsorsRowWeightSum, rowCount, sponsorsRowsAdded);
                sponsorsContainer.addView(sponsorsRow);
            }

            ImageView imageView = createSponsorImageView(sponsor);
            sponsorsRow.addView(imageView);

            sponsorsAddedInRow++;
        }
    }

    private ImageView createSponsorImageView(Sponsor sponsor) {
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewParams.weight = 1;
        imageViewParams.setMargins(5, 5, 5, 5);
        imageViewParams.gravity = Gravity.CENTER;


        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(imageViewParams);
        imageLoader.displayImage(sponsor.getImage(), imageView);
        return imageView;
    }

    private LinearLayout getSponsorsRowLinearLayout(int lastSponsorsRowWeightSum, int rowCount, int sponsorsRowsAdded) {
        LinearLayout sponsorsRow;
        sponsorsRow = new LinearLayout(context);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        sponsorsRow.setOrientation(LinearLayout.HORIZONTAL);
        sponsorsRow.setLayoutParams(rowParams);
        sponsorsRow.setWeightSum((sponsorsRowsAdded != rowCount) ? SPONSORS_IN_ROW : lastSponsorsRowWeightSum);
        return sponsorsRow;
    }

    public void generateContactsLayout(LinearLayout contactsContainer, ForeignCollection<Contact> contacts) {
        int contactsCount = contacts.size();
        if (contactsCount > 0) {
            contactsContainer.setVisibility(View.VISIBLE);
        }

        LinearLayout contactsRow = null;
        int lastContactsRowWeightSum = contactsCount % CONTACTS_IN_ROW;
        int rowCount = (contactsCount + CONTACTS_IN_ROW - 1) / CONTACTS_IN_ROW;
        int contactsRowsAdded = 0;
        int contactsAddedInRow = 0;

        for (Contact contact : contacts) {
            if (contactsAddedInRow == 0 || contactsAddedInRow == CONTACTS_IN_ROW) {
                contactsAddedInRow = 0;  //new row
                contactsRowsAdded++;
                contactsRow = getContactsRowLinearLayout(lastContactsRowWeightSum, rowCount, contactsRowsAdded);
                contactsContainer.addView(contactsRow);
            }

            LinearLayout contactContainer = createSingleContactContainer();

            TextView contactFullName = createContactFieldTextView(contact.getName() + contact.getSurname());
            contactContainer.addView(contactFullName);

            TextView contactEmail = createContactFieldTextView(contact.getEmail());
            contactContainer.addView(contactEmail);

            TextView conctactPhone = createContactFieldTextView(contact.getPhone());
            contactContainer.addView(conctactPhone);

            contactsRow.addView(contactContainer);

            contactsAddedInRow++;
        }
    }

    private TextView createContactFieldTextView(String s) {
        TextView contactName = new TextView(context);
        LinearLayout.LayoutParams contactNameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        contactNameParams.setMargins(0, 0, 0, 5);
        contactName.setGravity(Gravity.CENTER);
        contactName.setText(s);
        contactName.setTextSize(14);
        return contactName;
    }

    private LinearLayout createSingleContactContainer() {
        LinearLayout.LayoutParams contactLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        contactLayoutParams.weight = 1;
        contactLayoutParams.setMargins(5, 5, 5, 5);

        LinearLayout contactContainer = new LinearLayout(context);
        contactContainer.setOrientation(LinearLayout.VERTICAL);
        contactContainer.setLayoutParams(contactLayoutParams);

        return contactContainer;
    }

    private LinearLayout getContactsRowLinearLayout(int lastContactsRowWeightSum, int rowCount, int contactsRowsAdded) {
        LinearLayout contactsRow = new LinearLayout(context);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        contactsRow.setOrientation(LinearLayout.HORIZONTAL);
        contactsRow.setLayoutParams(rowParams);
        contactsRow.setWeightSum((contactsRowsAdded != rowCount) ? CONTACTS_IN_ROW : lastContactsRowWeightSum);

        return contactsRow;
    }
}
