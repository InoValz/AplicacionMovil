import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionmovil.Comentario
import com.example.aplicacionmovil.Publicacion
import com.example.aplicacionmovil.R

class ComentariosAdapter : ListAdapter<Comentario, ComentariosAdapter.ComentariosViewHolder>(DIFF_CALLBACK) {

    inner class ComentariosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoComentario: TextView = itemView.findViewById(R.id.textoComentario)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comentario>() {
            override fun areItemsTheSame(oldItem: Comentario, newItem: Comentario): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comentario, newItem: Comentario): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentariosViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentarios, parent, false)
        return ComentariosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComentariosViewHolder, position: Int) {
        val comentario = getItem(position)
        holder.textoComentario.text = comentario.texto
    }
}





