import { displayUnit } from '../../api/inventory'
import type { InventoryCategory, InventoryItem } from '../../types'

type InventoryTableProps = {
  items: InventoryItem[]
  category: InventoryCategory
  editingItem: string | null
  restockQuantity: string
  restocking: boolean
  onEdit: (name: string) => void
  onQuantityChange: (quantity: string) => void
  onSave: (item: InventoryItem) => void
  onCancel: () => void
}

export default function InventoryTable({
  items, category, editingItem, restockQuantity, restocking,
  onEdit, onQuantityChange, onSave, onCancel,
}: InventoryTableProps) {
  return (
    <div className="table-wrap">
      <table>
        <thead><tr><th>Inventory item</th><th>Amount on hand</th><th>Restock threshold</th><th>Status</th></tr></thead>
        <tbody>{items.map((item) => {
          const unit = displayUnit(item.unit, category)
          const low = item.quantity !== undefined && item.quantity <= item.restockThreshold
          return <tr key={item.name}>
            <td className="item-name">{item.name}</td>
            <td>
              <div className="quantity-cell">
                <span className="amount">{item.quantity ?? '—'} {unit && <span>{unit}</span>}</span>
                {editingItem === item.name ? (
                  <form className="restock-form" onSubmit={(event) => { event.preventDefault(); onSave(item) }}>
                    <label className="sr-only" htmlFor={`restock-${category}-${item.name}`}>Amount to add to {item.name}</label>
                    <input
                      id={`restock-${category}-${item.name}`}
                      type="number"
                      min="1"
                      step="1"
                      value={restockQuantity}
                      onChange={(event) => onQuantityChange(event.target.value)}
                      placeholder="Qty"
                      required
                    />
                    <button className="small-action save-action" type="submit" disabled={restocking}>Save</button>
                    <button className="small-action cancel-action" type="button" disabled={restocking} onClick={onCancel}>Cancel</button>
                  </form>
                ) : (
                  <button className="edit-action" type="button" onClick={() => onEdit(item.name)}>Add</button>
                )}
              </div>
            </td>
            <td className="amount">{item.restockThreshold} {unit && <span>{unit}</span>}</td>
            <td><span className={`status ${item.quantity === undefined ? 'status-unavailable' : low ? 'status-low' : 'status-ok'}`}>{item.quantity === undefined ? 'Count unavailable' : low ? 'Restock' : 'In stock'}</span></td>
          </tr>
        })}</tbody>
      </table>
      {items.length === 0 && <p className="empty-state">No inventory items found.</p>}
    </div>
  )
}
