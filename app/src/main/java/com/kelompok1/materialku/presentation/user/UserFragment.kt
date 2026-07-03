package com.kelompok1.materialku.presentation.user

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kelompok1.materialku.R
import com.kelompok1.materialku.databinding.DialogUserFormBinding
import com.kelompok1.materialku.databinding.FragmentUserBinding
import com.kelompok1.materialku.domain.model.RoleEnum
import com.kelompok1.materialku.domain.model.User
import com.kelompok1.materialku.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(
    FragmentUserBinding::inflate
) {
    private val viewModel: UserViewModel by viewModels()

    private val adapter by lazy {
        UserAdapter(
            onClick = { showForm(it) },
            onLongClick = { confirmDelete(it) }
        )
    }

    override fun setupViews() {
        binding.rvUser.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUser.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabAdd.setOnClickListener {
            showForm(null)
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { adapter.submitList(it.items) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { handleEvent(it) }
            }
        }
    }

    private fun handleEvent(event: UserEvent) {
        when (event) {
            UserEvent.Saved -> toast(getString(R.string.success_save))
            UserEvent.Deleted -> toast(getString(R.string.success_delete))
            is UserEvent.ValidationError -> toast(event.message)
        }
    }

    private fun showForm(existing: User?) {
        val db = DialogUserFormBinding.inflate(layoutInflater)

        db.etUsername.setText(existing?.username.orEmpty())
        db.switchAktif.isChecked = existing?.aktif ?: true

        db.tilPassword.helperText = if (existing == null) {
            getString(R.string.user_form_password_helper_new)
        } else {
            getString(R.string.user_form_password_helper_edit)
        }

        val roles = RoleEnum.values().toList()
        val roleLabels = roles.map { it.displayName() }
        val roleAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            roleLabels
        )
        db.actRole.setAdapter(roleAdapter)
        val initialRole = existing?.role ?: RoleEnum.ROLE_KASIR
        db.actRole.setText(initialRole.displayName(), false)

        val title = if (existing == null) R.string.user_add else R.string.user_edit

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(db.root)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val chosenLabel = db.actRole.text?.toString().orEmpty()
                val role = roles.firstOrNull { it.displayName() == chosenLabel } ?: initialRole
                viewModel.save(
                    UserFormInput(
                        editingId = existing?.id,
                        username = db.etUsername.text?.toString().orEmpty(),
                        password = db.etPassword.text?.toString().orEmpty(),
                        role = role,
                        aktif = db.switchAktif.isChecked
                    )
                )
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.user_delete_title)
            .setMessage(getString(R.string.user_delete_message, user.username))
            .setPositiveButton(R.string.action_hapus) { _, _ ->
                viewModel.delete(user.id)
            }
            .setNegativeButton(R.string.action_batal, null)
            .show()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
