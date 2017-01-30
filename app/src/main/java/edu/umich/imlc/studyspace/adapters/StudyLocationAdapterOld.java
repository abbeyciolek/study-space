package edu.umich.imlc.studyspace.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.StudyLocation;

/**
 * Created by Abbey Ciolek on 2/14/15.
 */
public class StudyLocationAdapterOld
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(int position, View view);
    }

    private static final String TAG = "StudyLocationAdapterOld";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM   = 1;

    private static int mScreenWidth;
    private static int mHeaderHeight;

    private Context             mContext;
    private List<StudyLocation> mStudyLocationList;
    private View                mHeader;
    private OnItemClickListener mListener;

    public StudyLocationAdapterOld(Context context,
                                   List<StudyLocation> studyLocationList,
                                   View header,
                                   int screenWidth,
                                   int headerHeight,
                                   OnItemClickListener listener) {
        mContext = context;
        mStudyLocationList = studyLocationList;
        mHeader = header;
        mScreenWidth = screenWidth;
        mHeaderHeight = headerHeight;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_HEADER) {
            if (mHeader == null) {
                throw new IllegalArgumentException("header should not be null");
            }
            viewHolder = new HeaderViewHolder(mHeader);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.study_location_list_item, parent, false);
            viewHolder = new ItemViewHolder(view);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(viewHolder.getPosition(), v);
                }
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

            // ensure that the header is the correct height
            headerViewHolder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(mScreenWidth,
                                                                                       mHeaderHeight));
            headerViewHolder.itemView.requestLayout();
        }
        else if (viewHolder instanceof ItemViewHolder) {
            ItemViewHolder studyViewHolder = (ItemViewHolder) viewHolder;
            StudyLocation location = mStudyLocationList.get(position - 1);

            studyViewHolder.studyLocationName.setText(location.getName());
            studyViewHolder.studyLocationBuildingName.setText(location.getBuildingName());
            studyViewHolder.studyLocationRatingBar.setRating(location.getRating());
            studyViewHolder.numReviews.setText("(" +location.getNumReviews() + ")");

            // use glide to load images from parse
            Uri avatarUri = Uri.parse(location.getImage().getUrl());
            Glide.with(mContext)
                 .load(avatarUri)
                 .diskCacheStrategy(DiskCacheStrategy.NONE)
                 .transform(new CircleBorderTransformation(mContext))
                 .into(studyViewHolder.studyLocationAvatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mStudyLocationList.size() + 1;
    }

    public StudyLocation getStudyLocation(int position) {
        return mStudyLocationList.get(position - 1);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_header);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView studyLocationAvatar;
        public TextView  studyLocationName;
        public TextView  studyLocationBuildingName;
        public TextView  numReviews;
        public RatingBar studyLocationRatingBar;

        public ItemViewHolder(View itemView) {
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
