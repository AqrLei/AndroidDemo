package open.aqrlei.com.coordinatorsample

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_recycler_item.view.*

/**
 * @author  aqrLei on 2018/7/20
 */
class RecyclerSimpleAdapter(private val context: Context, private val data: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater
            by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecyclerViewHolder(inflater.inflate(R.layout.layout_recycler_item, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.recyclerItemTv.text = data[position]
    }

    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}