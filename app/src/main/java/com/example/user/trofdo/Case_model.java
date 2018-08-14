package com.example.user.trofdo;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class Case_model extends RecyclerView.ViewHolder {

    String name;
    String image;

    public Case_model(View itemView) {
        super(itemView);
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
