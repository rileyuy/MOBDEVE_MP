package com.uy.esquivel.mobdeve_mp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uy.esquivel.mobdeve_mp.R;
import com.uy.esquivel.mobdeve_mp.model.Score;

import java.util.ArrayList;


public class ScoreAdapter
        extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private ArrayList<Score> scoresArrayList;
    private Context context;

    public ScoreAdapter(Context context, ArrayList<Score> scoresArrayList) {
        this.scoresArrayList = scoresArrayList;
        this.context = context;
    }

    public void addScores(ArrayList<Score> scoresArrayList){
        this.scoresArrayList.clear();
        this.scoresArrayList.addAll(scoresArrayList);
        notifyDataSetChanged();
    }

    public void addScore(Score score){
        scoresArrayList.add(0, score);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return scoresArrayList.size();
    }

    public ScoreAdapter.ScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_list_item, parent, false);
        ScoreViewHolder userViewHolder = new ScoreViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(ScoreAdapter.ScoreViewHolder holder, int position) {
        holder.tv_name.setText(scoresArrayList.get(position).getName());
        holder.tv_score.setText(scoresArrayList.get(position).getScore()+"");
    }

    protected class ScoreViewHolder extends RecyclerView.ViewHolder{
        TextView tv_score;
        TextView tv_name;

        public ScoreViewHolder(View view){
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_score = view.findViewById(R.id.tv_score);
        }
    }
}
