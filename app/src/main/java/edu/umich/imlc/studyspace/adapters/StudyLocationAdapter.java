package edu.umich.imlc.studyspace.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.StudyLocation;

/**
 * Created by Abbey Ciolek on 2/18/15.
 */
public class StudyLocationAdapter
        extends RecyclerView.Adapter<StudyLocationAdapter.StudyLocationViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(int position, View view);
    }

    private List<StudyLocation> mStudyLocations;
    private Context             mContext;
    private OnItemClickListener mListener;

    public StudyLocationAdapter(List<StudyLocation> studyLocations,
                                Context context,
                                OnItemClickListener listener) {
        mStudyLocations = studyLocations;
        mContext = context;
        mListener = listener;
    }

    @Override
    public StudyLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.study_location_list_item, parent, false);
        final StudyLocationViewHolder holder = new StudyLocationViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onItemClick(holder.getAdapterPosition(), v);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(StudyLocationViewHolder holder, int position) {
        StudyLocation location = mStudyLocations.get(position);

        holder.studyLocationName.setText(location.getName());
        holder.studyLocationBuildingName.setText(location.getBuildingName());
        holder.studyLocationRatingBar.setRating(location.getRating());
        holder.numReviews.setText("(" + location.getNumReviews() + ")");

        // use glide to load images from parse
        Uri avatarUri = Uri.parse(location.getImage().getUrl());
        Glide.with(mContext)
             .load(avatarUri)
             .diskCacheStrategy(DiskCacheStrategy.NONE)
             .transform(new CircleBorderTransformation(mContext))
             .into(holder.studyLocationAvatar);
    }

    @Override
    public int getItemCount() {
        return mStudyLocations.size();
    }

    public StudyLocation getStudyLocation(int position) {
        return mStudyLocations.get(position);
    }

    public static class StudyLocationViewHolder extends RecyclerView.ViewHolder {

        public ImageView studyLocationAvatar;
        public TextView  studyLocationName;
        public TextView  studyLocationBuildingName;
        public TextView  numReviews;
        public RatingBar studyLocationRatingBar;

        public StudyLocationViewHolder(View itemView) {
            super(itemView);

            studyLocationAvatar = (ImageView) itemView.findViewById(R.id.study_location_avatar);
            studyLocationName = (TextView) itemView.findViewById(R.id.study_location_name);
            studyLocationBuildingName =
                    (TextView) itemView.findViewById(R.id.study_location_building_name);
            studyLocationRatingBar = (RatingBar) itemView.findViewById(R.id.study_location_rating);
            numReviews = (TextView) itemView.findViewById(R.id.num_reviews);
        }
    }
}
