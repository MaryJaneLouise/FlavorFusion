package com.mariejuana.flavorfusion.data.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.mariejuana.flavorfusion.data.constants.API
import com.mariejuana.flavorfusion.data.helpers.realm.RealmDatabase
import com.mariejuana.flavorfusion.data.models.meals.Meal
import com.mariejuana.flavorfusion.databinding.ContentMealRvBinding
import java.io.Serializable

class MealAdapter(private var mealList: ArrayList<Meal>, private var context: Context, var mealAdapterCallback: MealAdapterInterface): RecyclerView.Adapter<MealAdapter.MealViewHolder>(), Serializable {
    private var database = RealmDatabase()
    // private lateinit var auth : FirebaseAuth

    interface MealAdapterInterface {
        fun addFaveFood(username: String, meal: Meal)
    }

    inner class MealViewHolder(private val binding: ContentMealRvBinding): RecyclerView.ViewHolder(binding.root) {
//        val currentUser = auth.currentUser
//        val username = currentUser?.email.toString()

        fun bind(itemData: Meal) {
            with(binding) {
                txtMealName.text = itemData.strMeal
//                if (itemData.strCategory == "null") {
//                    txtMealCategory.visibility = View.GONE
//                } else {
//                    txtMealCategory.visibility = View.VISIBLE
//                    txtMealCategory.text = itemData.strCategory
//                }
//
//                if (itemData.strArea == "null") {
//                    txtMealArea.visibility = View.GONE
//                } else {
//                    txtMealArea.visibility = View.VISIBLE
//                    txtMealArea.text = itemData.strArea
//                }

                Glide.with(context)
                    .load(itemData.strMealThumb)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .override(200,200)
                    .into(binding.mealImage)

                buttonFaveFood.setOnClickListener {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to add this food to the favorites?")
                    builder.setTitle("Warning!")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mealList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        //mealAdapterCallback.addFaveFood(username, itemData)
                        Toast.makeText(context, "The selected food has been placed in favorites.", Toast.LENGTH_SHORT).show()
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
        val binding = ContentMealRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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