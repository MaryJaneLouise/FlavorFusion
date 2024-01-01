package com.mariejuana.flavorfusion.data.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.ContentFaveMealRvBinding
import com.mariejuana.flavorfusion.ui.screens.food.selected.SelectedFoodInformationFragment
import java.io.Serializable

class FaveMealAdapter(private var mealList: ArrayList<Meal>, private var context: Context, var mealAdapterCallback: MealAdapterInterface): RecyclerView.Adapter<FaveMealAdapter.MealViewHolder>(), Serializable {
    private var buttonsVisible = false

    interface MealAdapterInterface {
        fun removeFaveFood(username: String, meal: Meal)
    }

    inner class MealViewHolder(private val binding: ContentFaveMealRvBinding): RecyclerView.ViewHolder(binding.root) {
        val sharedPref = context.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        fun bind(itemData: Meal) {
            buttonsVisible = false
            with(binding) {
                showButtonUnfaveShow.visibility = View.GONE

                txtMealName.text = itemData.strMeal

                cvMeal.setOnClickListener {
                    if (!buttonsVisible) {
                        showButtonUnfaveShow.visibility = View.VISIBLE

                        buttonsVisible = true
                    } else {
                        showButtonUnfaveShow.visibility = View.GONE

                        buttonsVisible = false
                    }
                }

                Glide.with(context)
                    .load(itemData.strMealThumb)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(200,200)
                    .transform(CenterCrop(), RoundedCorners(25))
                    .into(binding.mealImage)

                buttonShowDetails.setOnClickListener {
                    var a  = SelectedFoodInformationFragment()
                    val manager: FragmentManager =
                        (context as AppCompatActivity).supportFragmentManager
                    val bundle = Bundle()
                    bundle.putString("FoodName", itemData.strMeal);
                    a.arguments = bundle
                    a.show(manager,"")
                }

                buttonUnfaveFood.setOnClickListener {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to remove this food to the favorites?")
                    builder.setTitle("Warning!")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mealList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        if (username != null) {
                            mealAdapterCallback.removeFaveFood(username, itemData)
                        }
                        Toast.makeText(context, "The selected food has been removed in favorites.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ContentFaveMealRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val mealData = mealList[position]
        holder.bind(mealData)
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    fun updateMeal(mealList: ArrayList<Meal>){
        this.mealList = arrayListOf()
        notifyDataSetChanged()
        this.mealList = mealList
        this.notifyItemInserted(this.mealList.size)
    }
}