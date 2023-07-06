package edu.mx.unipolidgo.myfirebaseproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.mx.unipolidgo.myfirebaseproject.databinding.ActivityViewMenuBinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewMenu : AppCompatActivity() {

    data class Product(
        val description: String,
        val price: String,
        val size: String,
        val image: String,
        val name: String
    )

    private var binding: ActivityViewMenuBinding? = null
    private val products: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_view_menu)

        binding = ActivityViewMenuBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@ViewMenu)
            adapter = ProductAdapter(products)
        }

        readData()
    }
    class FirebaseUtils {
        val firestoreDatabase = FirebaseFirestore.getInstance()
    }

    private fun readData(){
        DataActivity.FirebaseUtils().firestoreDatabase.collection("platillos")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val description = document.getString("Description")
                    val price = document.getString("Price")
                    val size = document.getString("Size")
                    val image = document.getString("Image")
                    val name = document.getString("Name")

                    if (description != null && price != null && size != null && image != null && name != null) {
                        val product = Product(description, price, size, image, name)
                        products.add(product)
                    }
                }

                binding!!.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener{
                Log.w(TAG,"Los documentos no se pudieron recuperar {${it.message}}")
                Toast.makeText(this, "Los documentos no se pudieron recuperar",
                    Toast.LENGTH_LONG).show()
            }
    }

    class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.textName)
            val priceTextView: TextView = itemView.findViewById(R.id.textPrice)
            val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
            val sizeTextView: TextView = itemView.findViewById(R.id.textSize)
            val ImageImageView: ImageView = itemView.findViewById(R.id.imageProduct)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_menu, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = products[position]
            holder.nameTextView.text = product.name
            holder.priceTextView.text = product.price
            holder.descriptionTextView.text = product.description
            holder.sizeTextView.text = product.size
        }

        override fun getItemCount(): Int {
            return products.size
        }
    }
}
