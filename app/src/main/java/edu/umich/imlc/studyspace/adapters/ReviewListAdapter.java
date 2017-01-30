package edu.umich.imlc.studyspace.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import edu.umich.imlc.studyspace.R;
import edu.umich.imlc.studyspace.model.Review;
import edu.umich.imlc.studyspace.ui.StudyLocationDetail.CharCircleIcon;

/**
 * Created by Abbey Ciolek on 3/10/15.
 */
public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private static final String TAG = ReviewListAdapter.class.getName();

    public interface VoteClickListener {

        void onVoteClick(Review review, View view);
    }

    private List<Review> mReviews;
    private Context      mContext;
    private VoteClickListener mVoteListener;

    private DateFormat mFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    private static Typeface sIconTypeface = Typeface.create("sans-serif-light", Typeface.NORMAL);

    public ReviewListAdapter(List<Review> reviews,
                             Context context,
                             VoteClickListener listener) {
        mReviews = reviews;
        mContext = context;
        mVoteListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review review = mReviews.get(holder.getAdapterPosition());
                /*AlertDialog dialog = mVoteListener.onVoteClick(review, v);
                dialog.show();*/
                mVoteListener.onVoteClick(review, v);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mReviews.get(position);

        String userName = review.getUser().getString("Name");
        holder.avatar.setBackground(new CharCircleIcon(userName.charAt(0),
                                                       review.getUser().getUsername(),
                                                       sIconTypeface,
                                                       mContext.getResources()));
        holder.name.setText(userName);
        holder.date.setText(mFormatter.format(review.getUpdatedAt()));
        holder.ratingBar.setRating(review.getRating());
        holder.title.setText(review.getTitle());
        holder.details.setText(review.getDetail());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View      avatar;
        public TextView  name;
        public TextView  date;
        public RatingBar ratingBar;
        public TextView  title;
        public TextView  details;
        public ImageButton vote;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.review_avatar);
            name = (TextView) itemView.findViewById(R.id.review_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.review_rating);
            title = (TextView) itemView.findViewById(R.id.review_title);
            details = (TextView) itemView.findViewById(R.id.review_details);
            date = (TextView) itemView.findViewById(R.id.review_date);
            vote = (ImageButton) itemView.findViewById(R.id.vote);
        }
    }

}
