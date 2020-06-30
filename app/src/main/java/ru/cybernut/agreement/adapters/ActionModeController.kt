package ru.cybernut.agreement.adapters

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode

import androidx.recyclerview.selection.SelectionTracker
import ru.cybernut.agreement.LoginActivity
import ru.cybernut.agreement.R

class ActionModeController(private val tracker: SelectionTracker<*>, private val fragment: Approvable) : ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        tracker.clearSelection()
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = true

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_approve_selected -> {
            fragment.ApproveSelected()
            mode.finish()
            true
        }
        else -> false
    }


    interface Approvable {
        fun ApproveSelected()
    }
}