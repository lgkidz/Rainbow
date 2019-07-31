package com.OdiousPanda.rainbow.Utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.OdiousPanda.rainbow.DataModel.Food;
import com.OdiousPanda.rainbow.MainFragments.DetailsFragment;
import com.OdiousPanda.rainbow.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FoodUtil {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Food> foods = new ArrayList<>();
    private List<Field> foodIcons = new ArrayList<>();

    public FoodUtil() {
        Field[] drawablesFields = com.OdiousPanda.rainbow.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                if (field.getName().contains("dish") || field.getName().contains("dessert") || field.getName().contains("drink")) {
                    foodIcons.add(field);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        queryFoods();
    }

    private int getIcons(String attribute) {

        List<Integer> icons = new ArrayList<>();
        for (Field field : foodIcons) {
            try {
                if (field.getName().contains(attribute)) {
                    icons.add(field.getInt(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (icons.size() == 0) {
            return R.drawable.dish_ic_dish;
        }
        return icons.get(new Random().nextInt(icons.size()));
    }

    private void queryFoods() {
        db.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Food food = document.toObject(Food.class);
                                foods.add(food);
                            }
                            updateNewFood();
                        } else {
                            Log.d("food", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateNewFood() {
        if (foods.size() == 0) {
            queryFoods();
            return;
        }
        Food food = foods.get(new Random().nextInt(foods.size()));
        int icon = getIcons(food.getAtt());
        DetailsFragment.getInstance().updateFoodData(icon, food.getName());
    }
}
