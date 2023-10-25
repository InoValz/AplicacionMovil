import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionmovil.Publicacion
import com.example.aplicacionmovil.R

class PublicacionAdapter(private val publicacionesList: List<Publicacion>) :
    RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder>() {

    inner class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        val ubicacionTextView: TextView = itemView.findViewById(R.id.ubicacionTextView)
        val fechaHoraTextView: TextView = itemView.findViewById(R.id.fechaHoraTextView)
        val CategoriaTextView: TextView = itemView.findViewById(R.id.CategoriaTextView)
        // Agrega más vistas según sea necesario
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return PublicacionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicacionesList[position]

        holder.tituloTextView.text = publicacion.titulo
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion

        val fechaHora = "${publicacion.fecha} ${publicacion.hora}"
        holder.fechaHoraTextView.text = fechaHora

        holder.CategoriaTextView.text = publicacion.categoria
        // Configura más vistas según sea necesario
    }

    override fun getItemCount(): Int {
        return publicacionesList.size
    }
}


