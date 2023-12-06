import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionmovil.R

class ComentariosAdapter : RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoComentario: TextView = itemView.findViewById(R.id.textoComentario)
    }

    private val comentariosList: MutableList<String> = mutableListOf()

    fun setComentarios(comentarios: List<String>) {
        comentariosList.clear()
        comentariosList.addAll(comentarios)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_comentarios, parent, false)
        return ComentarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentariosList[position]
        holder.textoComentario.text = comentario
    }

    override fun getItemCount(): Int {
        return comentariosList.size
    }
}




