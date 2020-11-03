package org.feup.cmov.acmeclient.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.feup.cmov.acmeclient.adapter.ClickListener
import org.feup.cmov.acmeclient.adapter.Content
import org.feup.cmov.acmeclient.adapter.ItemListAdapter
import org.feup.cmov.acmeclient.data.Status
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val adapter = ItemListAdapter(
//            object : ClickListener<Item> {
//                override fun onClick(target: Item) {
//                    viewModel.toggleItem(target)
//                }
//            },
            object : ClickListener<Item?> {
                override fun onClick(target: Item?) {
                    ItemDialogFragment(target) { item, quantity ->
                        viewModel.changeItemQuantity(
                            item,
                            quantity
                        )
                    }.show(requireActivity().supportFragmentManager, ItemDialogFragment.TAG)
//                    showDialog(target)
                }
            }
        )

        binding.homeRecyclerView.adapter = adapter
        binding.homeRefreshLayout.setOnRefreshListener { viewModel.fetchItems() }

        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: ItemListAdapter) {
        viewModel.refreshing.observe(viewLifecycleOwner, {
            val refreshing = it ?: return@observe
            binding.homeRefreshLayout.isRefreshing = refreshing
        })

        viewModel.items.observe(viewLifecycleOwner) {
            val items = it ?: return@observe

            binding.homeRefreshLayout.isRefreshing = items.status == Status.LOADING

            if (items.status == Status.SUCCESS)
                adapter.submitList(items.data)
        }
    }

//    private fun showDialog(item: Item?) {
//        val downloadDialog = AlertDialog.Builder(requireActivity())
//        downloadDialog.setTitle(title)
//        downloadDialog.setMessage(message)
//        downloadDialog.setPositiveButton(buttonYes) { di: DialogInterface?, i: Int ->
//            val uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android")
//            val intent = Intent(Intent.ACTION_VIEW, uri)
//            act.startActivity(intent)
//        }
//        downloadDialog.setNegativeButton(buttonNo, null)
//        return downloadDialog.show()
//        val inflater = requireActivity().layoutInflater

//        val bundle = Bundle()
//        bundle.putString("name", "This is name")
//        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Select quantity")
////            .setMessage(item?.price.toString())
////            .setIcon(R.drawable.ic_baseline_star_outline_24)
//            .setView(R.layout.number_picker_dialog)
//
////            .setNeutralButton("Neutral") { dialog, which ->
////                // Respond to neutral button press
////            }
////            .setNegativeButton("Decline") { dialog, which ->
////                // Respond to negative button press
////            }
//            .setPositiveButton("Save") { dialog, which ->
//                // Respond to positive button press
//                if (BUTTON_POSITIVE == which) {
//                    dialog.findViewById<NumberPickerWithXml>(R.id.number_picker)
//                }
//            }
//    }
}