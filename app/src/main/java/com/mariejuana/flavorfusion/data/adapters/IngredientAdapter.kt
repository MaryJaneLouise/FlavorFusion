package com.mariejuana.flavorfusion.data.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.models.meals.Ingredient
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.ContentIngredientsRvBinding
import com.mariejuana.flavorfusion.databinding.ContentMealRvBinding
import java.io.Serializable

class IngredientAdapter(private var ingredientList: ArrayList<Ingredient>, private var context: Context): RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>(), Serializable {
    inner class IngredientViewHolder(private val binding: ContentIngredientsRvBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(itemData: Ingredient) {
            with(binding) {
                txtIngredientName.text = itemData.strIngredient
            }

            Glide.with(context)
                .load(API.INGREDIENTS_IMG_URL + itemData.strIngredient + ".png")
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(200,200)
                .into(binding.mealImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ContentIngredientsRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredientData = ingredientList[position]
        holder.bind(ingredientData)
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    fun updateIngredients(ingredientList: ArrayList<Ingredient>){
        this.ingredientList = arrayListOf()
        notifyDataSetChanged()
        this.ingredientList = ingredientList
        this.notifyItemInserted(this.ingredientList.size)
    }
}