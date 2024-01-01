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
import com.mariejuana.flavorfusion.data.models.meals.custom.CustomMeal
import com.mariejuana.flavorfusion.databinding.ContentCustomMealRvBinding
import com.mariejuana.flavorfusion.ui.screens.food.dialogs.EditCustomMealDialog
import com.mariejuana.flavorfusion.ui.screens.food.selected.CustomFoodInformationFragment
import java.io.Serializable

class CustomMealAdapter(
    private var mealList: ArrayList<CustomMeal>,
    private var context: Context,
    var mealAdapterCallback: MealAdapterInterface,
    private val refreshDataInterface: EditCustomMealDialog.RefreshDataInterface
): RecyclerView.Adapter<CustomMealAdapter.MealViewHolder>(), Serializable {
    private var buttonsVisible = false

    interface MealAdapterInterface {
        fun removeCustomFood(username: String, meal: CustomMeal)
    }

    inner class MealViewHolder(private val binding: ContentCustomMealRvBinding): RecyclerView.ViewHolder(binding.root) {
        val sharedPref = context.getSharedPreferences("username_login", Context.MODE_PRIVATE)
        val username = sharedPref?.getString("username", "defaultUsername")

        fun bind(itemData: CustomMeal) {
            buttonsVisible = false
            with(binding) {
                showButtonDelete.visibility = View.GONE

                txtMealName.text = itemData.name
                txtMealCategory.text = itemData.category

                cvMeal.setOnClickListener {
                    if (!buttonsVisible) {
                        showButtonDelete.visibility = View.VISIBLE

                        buttonsVisible = true
                    } else {
                        showButtonDelete.visibility = View.GONE

                        buttonsVisible = false
                    }
                }

                buttonUpdateFood.setOnClickListener {
                    var a  = EditCustomMealDialog()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("EditCustomFoodId", itemData.id)
                    bundle.putString("EditCustomFoodName", itemData.name)
                    bundle.putString("EditCustomFoodCategory", itemData.category)
                    bundle.putString("EditCustomFoodInstructions", itemData.instruction)
                    bundle.putString("EditCustomFoodIngredients", itemData.ingredient)

                    a.refreshDataCallback = refreshDataInterface
                    a.arguments = bundle
                    a.show(manager,"")
                }

                buttonShowFood.setOnClickListener {
                    var a  = CustomFoodInformationFragment()
                    val manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager

                    val bundle = Bundle()
                    bundle.putString("ShowCustomFoodId", itemData.id)
                    bundle.putString("ShowCustomFoodName", itemData.name)
                    bundle.putString("ShowCustomFoodCategory", itemData.category)
                    bundle.putString("ShowCustomFoodInstructions", itemData.instruction)
                    bundle.putString("ShowCustomFoodIngredients", itemData.ingredient)

                    a.arguments = bundle
                    a.show(manager,"")
                }

                buttonDeleteFood.setOnClickListener {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to delete this custom meal?")
                    builder.setTitle("Warning!")
                    builder.setPositiveButton("Yes") { dialog, _ ->
                        mealList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        if (username != null) {
                            mealAdapterCallback.removeCustomFood(username, itemData)
                        }
                        Toast.makeText(context, "The selected custom food has been deleted.", Toast.LENGTH_SHORT).show()
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
        val binding = ContentCustomMealRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val mealData = mealList[position]
        holder.bind(mealData)
    }

    override fun getItemCount(): Int {
        return mealList.size
    }

    fun updateMeal(mealList: ArrayList<CustomMeal>){
        this.mealList = arrayListOf()
        notifyDataSetChanged()
        this.mealList = mealList
        this.notifyItemInserted(this.mealList.size)
    }
}