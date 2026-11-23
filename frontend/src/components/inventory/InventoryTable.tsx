import type { FormEvent } from 'react'

import type { IngredientForecast, InventoryCategory, InventoryItem } from '../../types'

type InventoryTableProps = {
  items: InventoryItem[]
  forecasts: Record<string, IngredientForecast>
  category: InventoryCategory
  editingItem: string | null
  restockQuantity: string
  restocking: boolean
  onEdit: (name: string) => void
  onQuantityChange: (quantity: string) => void
  onSave: (item: InventoryItem) => void
  onCancel: () => void
}

type RestockFormProps = {
  item: InventoryItem
  category: InventoryCategory
  quantity: string
  restocking: boolean
  onQuantityChange: (quantity: string) => void
  onSave: (item: InventoryItem) => void
  onCancel: () => void
}

type InventoryRowProps = {
  item: InventoryItem
  forecast: IngredientForecast | undefined
  category: InventoryCategory
  editingItem: string | null
  restockQuantity: string
  restocking: boolean
  onEdit: (name: string) => void
  onQuantityChange: (quantity: string) => void
  onSave: (item: InventoryItem) => void
  onCancel: () => void
}

function displayUnit(unit: string | undefined, category: InventoryCategory) {
  if (category === 'UTENSILS') return 'ea'

  switch (unit?.toUpperCase()) {
    case 'KILOGRAMS':
      return 'kg'
    case 'LITERS':
      return 'L'
    case 'ITEMS':
      return 'ea'
    default:
      return unit ?? ''
  }
}

function inventoryStatus(item: InventoryItem) {
  if (item.quantity === undefined) {
    return { className: 'status-unavailable', label: 'Count unavailable' }
  }
  if (item.quantity <= item.restockThreshold) {
    return { className: 'status-low', label: 'Restock' }
  }
  return { className: 'status-ok', label: 'In stock' }
}

function restockInputId(category: InventoryCategory, itemName: string) {
  return `restock-${category}-${itemName}`.toLowerCase().replace(/[^a-z0-9-]+/g, '-')
}

function RestockForm({
  item,
  category,
  quantity,
  restocking,
  onQuantityChange,
  onSave,
  onCancel,
}: RestockFormProps) {
  const inputId = restockInputId(category, item.name)
  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    onSave(item)
  }

  return (
    <form className="restock-form" onSubmit={handleSubmit}>
      <label className="sr-only" htmlFor={inputId}>
        Amount to add to {item.name}
      </label>
      <input
        id={inputId}
        type="number"
        min="1"
        step="1"
        value={quantity}
        placeholder="Qty"
        required
        onChange={(event) => onQuantityChange(event.target.value)}
      />
      <button className="small-action save-action" type="submit" disabled={restocking}>
        Save
      </button>
      <button
        className="small-action cancel-action"
        type="button"
        disabled={restocking}
        onClick={onCancel}
      >
        Cancel
      </button>
    </form>
  )
}

function InventoryRow({
  item,
  forecast,
  category,
  editingItem,
  restockQuantity,
  restocking,
  onEdit,
  onQuantityChange,
  onSave,
  onCancel,
}: InventoryRowProps) {
  const unit = displayUnit(item.unit, category)
  const status = inventoryStatus(item)

  return (
    <tr>
      <td className="item-name">{item.name}</td>
      <td>
        <div className="quantity-cell">
          <span className="amount">
            {item.quantity ?? '—'} {unit && <span>{unit}</span>}
          </span>
          {editingItem === item.name ? (
            <RestockForm
              item={item}
              category={category}
              quantity={restockQuantity}
              restocking={restocking}
              onQuantityChange={onQuantityChange}
              onSave={onSave}
              onCancel={onCancel}
            />
          ) : (
            <button className="edit-action" type="button" onClick={() => onEdit(item.name)}>
              Add
            </button>
          )}
        </div>
      </td>
      <td className="amount">{item.restockThreshold} {unit && <span>{unit}</span>}</td>
      <td><span className={`status ${status.className}`}>{status.label}</span></td>
      <td className="forecast-cell">
        {category === 'UTENSILS'
          ? 'Not applicable'
          : forecast?.daysUntilThreshold === null
            ? 'No sales in last 7 days'
            : forecast
              ? forecast.daysUntilThreshold === 0
                ? <span className="forecast-now" title={`Average usage: ${forecast.averageDailyUsage} ${unit} per day`}>Restock now</span>
                : <span title={`Average usage: ${forecast.averageDailyUsage} ${unit} per day`}>~{forecast.daysUntilThreshold} {forecast.daysUntilThreshold === 1 ? 'day' : 'days'}</span>
              : '—'}
      </td>
    </tr>
  )
}

export default function InventoryTable({
  items,
  forecasts,
  category,
  editingItem,
  restockQuantity,
  restocking,
  onEdit,
  onQuantityChange,
  onSave,
  onCancel,
}: InventoryTableProps) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Inventory item</th>
            <th>Amount on hand</th>
            <th>Restock threshold</th>
            <th>Status</th>
            <th>Restock forecast</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <InventoryRow
              key={item.name}
              item={item}
              forecast={forecasts[item.name]}
              category={category}
              editingItem={editingItem}
              restockQuantity={restockQuantity}
              restocking={restocking}
              onEdit={onEdit}
              onQuantityChange={onQuantityChange}
              onSave={onSave}
              onCancel={onCancel}
            />
          ))}
        </tbody>
      </table>
      {items.length === 0 && <p className="empty-state">No inventory items found.</p>}
    </div>
  )
}
