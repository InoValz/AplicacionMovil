import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionmovil.Publicacion
import com.example.aplicacionmovil.R

class PublicacionAdapter(private var publicacionesList: List<Publicacion>) :
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
    fun refreshPublicaciones(nuevasPublicaciones: List<Publicacion>) {
        publicacionesList.clear()
        publicacionesList.addAll(nuevasPublicaciones)
        notifyDataSetChanged()
    }

    fun addPublicaciones(nuevasPublicaciones: List<Publicacion>) {
        publicacionesList.clear()  // Limpia la lista actual
        publicacionesList.addAll(nuevasPublicaciones)  // Agrega las nuevas publicaciones
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }

}

private fun <E> List<E>.addAll(nuevasPublicaciones: List<E>) {

}

private fun <E> List<E>.clear() {
    TODO("Not yet implemented")
}


