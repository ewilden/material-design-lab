package com.codepath.android.lollipopexercise.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.lollipopexercise.R;
import com.codepath.android.lollipopexercise.activities.DetailsActivity;
import com.codepath.android.lollipopexercise.models.Contact;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

// Provide the underlying view for an individual list item.
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.VH> {
    private Activity mContext;
    private List<Contact> mContacts;

    public ContactsAdapter(Activity context, List<Contact> contacts) {
        mContext = context;
        if (contacts == null) {
            throw new IllegalArgumentException("contacts must not be null");
        }
        mContacts = contacts;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(VH holder, int position) {
        Contact contact = mContacts.get(position);
        holder.rootView.setTag(contact);
        holder.tvName.setText(contact.getName());
        final ImageView ivProfile = holder.ivProfile;
        final View rootView = holder.rootView;
        final TextView tvName = holder.tvName;

        // Use Picasso to get a callback with a Bitmap which can then
        // be used to extract a vibrant color from the Palette.

        // Define a listener for image loading
        Target target = new Target() {
            // Fires when Picasso finishes loading the bitmap for the target
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // TODO 1. Insert the bitmap into the profile image view
                ivProfile.setImageBitmap(bitmap);

                // TODO 2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch vibrant = palette.getVibrantSwatch();
                        if (vibrant != null) {
                            rootView.setBackgroundColor(vibrant.getRgb());
                            tvName.setTextColor(vibrant.getTitleTextColor());
                        }
                    }
                });
                // Set the result as the background color for `R.id.vPalette` view containing the contact's name.
            }

            // Fires if bitmap fails to load
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        // TODO: Clear the bitmap and the background color in adapter
        holder.rootView.setBackgroundResource(0);
        //holder.tvName.setTextColor(0);

        // Store the target into the tag for the profile to ensure target isn't garbage collected prematurely
        holder.ivProfile.setTag(target);
        // Instruct Picasso to load the bitmap into the target defined above
        Picasso.with(mContext).load(contact.getThumbnailDrawable()).into(target);

        //Picasso.with(mContext).load(contact.getThumbnailDrawable()).fit().centerCrop().into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    // Provide a reference to the views for each contact item
    public final static class VH extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView ivProfile;
        final TextView tvName;
        final View vPalette;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            vPalette = itemView.findViewById(R.id.vPalette);

            // Navigate to contact details activity on click of card view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Contact contact = (Contact)v.getTag();
                    if (contact != null) {
                        // Fire an intent when a contact is selected
                        // Pass contact object in the bundle and populate details activity.
                        Intent i = new Intent(v.getContext(), DetailsActivity.class);
                        i.putExtra(DetailsActivity.EXTRA_CONTACT, contact);
                        Pair<View, String> profile = Pair.create((View) ivProfile, "profile");
                        Pair<View, String> name = Pair.create((View) tvName, "name");
                        Pair<View, String> palette = Pair.create((View) vPalette, "palette");
                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        (Activity) v.getContext(), profile, name, palette);

                        Bundle b = options.toBundle();
                        v.getContext().startActivity(i, b);
                    }
                }
            });
        }
    }

}
