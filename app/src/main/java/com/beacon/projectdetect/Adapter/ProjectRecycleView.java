package com.beacon.projectdetect.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beacon.projectdetect.R;
import com.beacon.projectdetect.Service.FirebaseManager;
import com.beacon.projectdetect.module.BeaconData;
import com.beacon.projectdetect.module.ProjectPlug;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by qiwhuang on 4/7/2017.
 */

public class ProjectRecycleView extends RecyclerView.Adapter<ProjectRecycleView.ViewHolder> {

    private final List<BeaconData> beaconDataList;
    private final BeaconInteractionCallback callback;
    private boolean changePhoto = false;

    public ProjectRecycleView(List<BeaconData> beaconDataList, BeaconInteractionCallback callback, boolean changePhoto){
        this.callback = callback;
        this.beaconDataList = beaconDataList;
        this.changePhoto = changePhoto;
    }
    @Override
    public ProjectRecycleView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new ProjectRecycleView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProjectRecycleView.ViewHolder holder, int position) {
        holder.beaconData = beaconDataList.get(position);

        // Set the  beacon identifier
        if (holder.beaconData != null && holder.beaconData.getBeaconIdentifier() != null) {
            holder.textBeacon.setText(String.valueOf(holder.beaconData.getBeaconIdentifier()));
        }

        // Set the project name

        for (ProjectPlug projectPlug : holder.beaconData.getProjectPlugs()){
            if(projectPlug.getProjectName() != ""){
                holder.textProjectName.setText(String.valueOf(projectPlug.getProjectName()));
                if(!projectPlug.getImageName().equals("")){
                    StorageReference storageReference = FirebaseManager.getInstance().getFirebaseStorage().getReference().child("BeaconData").child(holder.beaconData.getUid()).child("ProjectPlug").child(projectPlug.getProjectId()).child(projectPlug.getImageName());
                    if(changePhoto)
                        Glide.with(holder.view.getContext())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .placeholder(R.drawable.project)
                                .into(holder.imageView);
                    else
                        Glide.with(holder.view.getContext())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .placeholder(R.drawable.project)
                                .into(holder.imageView);
                }
            }
        }

        // Click on the view
        holder.view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onClickBeacon(holder.beaconData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return beaconDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView textProjectName;
        private final TextView textBeacon;
        private final ImageView imageView;
        private BeaconData beaconData;

        private ViewHolder(View view) {
            super(view);

            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.image_project);
            textProjectName = (TextView) view.findViewById(R.id.project_item_name);
            textBeacon = (TextView) view.findViewById(R.id.project_item_beacon);

            // Load Fonts
            TypeManager typefaceManager = TypeManager.getInstance();
            textProjectName.setTypeface(typefaceManager.getTypefaceRalewaySemibold());
            textBeacon.setTypeface(typefaceManager.getTypefaceRalewayRegular());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + String.valueOf(textProjectName.getText()) + "'";
        }
    }

    public interface BeaconInteractionCallback {
        void onClickBeacon(BeaconData item);
    }

}