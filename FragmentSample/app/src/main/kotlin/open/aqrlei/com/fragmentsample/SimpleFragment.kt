package open.aqrlei.com.fragmentsample


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author  aqrLei on 2018/7/25
 */
class SimpleFragment : Fragment() {
    companion object {

        fun newInstance() = SimpleFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_simple, container, false)
    }


}