package com.example.moviewbrowserort.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviewbrowserort.main.MovieDetails;
import com.example.moviewbrowserort.model.MovieModel;
import com.example.moviewbrowserort.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.NumberViewHolder> {

    private List<MovieModel> movieUtils;
    private Context context;

    public MovieAdapter(Context context,List<MovieModel> movieUtils) {
        this.context = context;
        this.movieUtils = movieUtils;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rv_movie_item, parent, false);
        return new NumberViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final NumberViewHolder holder, final int position) {

        holder.itemView.setTag(movieUtils.get(position));

        final MovieModel movieModel = movieUtils.get(position);

        holder.txtTitle.setText(movieModel.getMovieTitle());

        if (movieModel.getMoviePosterPath().equals("null")){
//            holder.imgPoster.setImageResource(R.drawable.profile23);
        }
        else {
            Picasso.get().load("https://image.tmdb.org/t/p/w500/" +  movieModel.getMoviePosterPath()).into(holder.imgPoster);
        }

        holder.cv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , MovieDetails.class);
                intent.putExtra("imgPath" , movieModel.getMoviePosterPath());
                intent.putExtra("id" , movieModel.getMovieId());
                intent.putExtra("title" , movieModel.getMovieTitle());
                intent.putExtra("synopsis" , movieModel.getMovieSynopsis());
                intent.putExtra("date" , movieModel.getMovieReleaseDate());
                intent.putExtra("rating" , movieModel.getMovieRating());
                context.startActivity(intent);
//                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieUtils.size();
    }

    static class NumberViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;
        ImageView imgPoster;
        CardView cv_details ;

        NumberViewHolder(View itemView) {
            super(itemView);

            imgPoster =  itemView.findViewById(R.id.img_moviePoster);
            txtTitle = itemView.findViewById(R.id.txt_movieTitle);
            cv_details = itemView.findViewById(R.id.cv_details);

        }

    }
}

