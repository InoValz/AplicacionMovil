import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionmovil.Publicacion
import com.example.aplicacionmovil.R
import com.google.firebase.database.FirebaseDatabase

class PublicacionAdapter : ListAdapter<Publicacion, PublicacionAdapter.PublicacionViewHolder>(DIFF_CALLBACK) {
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnComentariosButtonClickListener {
        fun onComentariosButtonClick(position: Int, tipoBoton: PublicacionAdapter.TipoBoton)
    }

    private var onItemClickListener: OnItemClickListener? = null
    private var onComentariosButtonClickListener: OnComentariosButtonClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    fun setOnComentariosButtonClickListener(listener: OnComentariosButtonClickListener) {
        this.onComentariosButtonClickListener = listener
    }

    inner class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
        val ubicacionTextView: TextView = itemView.findViewById(R.id.ubicacionTextView)
        val fechaHoraTextView: TextView = itemView.findViewById(R.id.fechaHoraTextView)
        val CategoriaTextView: TextView = itemView.findViewById(R.id.CategoriaTextView)
        val btn_IconoComentarios_escribir: ImageButton = itemView.findViewById(R.id.btn_IconoComentarios_escribir)
        val btn_IconoComentarios_leer: ImageButton = itemView.findViewById(R.id.btn_IconoComentarios_leer)
        val btnCambiarEstado: Button = itemView.findViewById(R.id.btnCambiarEstado)
        init {
            btn_IconoComentarios_escribir.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onComentariosButtonClickListener?.onComentariosButtonClick(position, PublicacionAdapter.TipoBoton.ESCRIBIR)
                }
            }

            btn_IconoComentarios_leer.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onComentariosButtonClickListener?.onComentariosButtonClick(position, TipoBoton.LEER)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Publicacion>() {
            override fun areItemsTheSame(oldItem: Publicacion, newItem: Publicacion): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Publicacion, newItem: Publicacion): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return PublicacionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = getItem(position)

        holder.tituloTextView.text = publicacion.titulo
        holder.descripcionTextView.text = publicacion.descripcion
        holder.ubicacionTextView.text = publicacion.ubicacion

        val fechaHora = "${publicacion.fecha} ${publicacion.hora}"
        holder.fechaHoraTextView.text = fechaHora

        holder.CategoriaTextView.text = publicacion.categoria

        val btnCambiarEstadoTexto = holder.itemView.findViewById<Button>(R.id.btnCambiarEstado)
        btnCambiarEstadoTexto.text = "Estado: ${if (publicacion.estado) "Revisado" else "No revisado"}"

        val btnCambiarEstado = holder.itemView.findViewById<Button>(R.id.btnCambiarEstado)

        if (!publicacion.estado) {
            val colorNoRevisado = ContextCompat.getColor(holder.itemView.context, R.color.colorNoRevisado)
            btnCambiarEstado.setBackgroundColor(colorNoRevisado)
        } else {
            val colorRevisado = ContextCompat.getColor(holder.itemView.context, R.color.colorRevisado)
            btnCambiarEstado.setBackgroundColor(colorRevisado)
        }

        btnCambiarEstado.setOnClickListener {
            publicacion.estado = !publicacion.estado

            guardarEstadoEnBaseDeDatos(publicacion.id, publicacion.estado)

            notifyItemChanged(position)
        }
    }
    private fun guardarEstadoEnBaseDeDatos(publicacionId: String, nuevoEstado: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("publicaciones")
        reference.child(publicacionId).child("estado").setValue(nuevoEstado)
    }

    enum class TipoBoton {
        ESCRIBIR, LEER
    }
}

