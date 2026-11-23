import { menuCategoryArt } from '../../types'
import type { MenuItem } from '../../types'

type MenuItemCardProps = {
  items: MenuItem[]
  quantities: Record<number, number>
  onChange: (item: MenuItem, quantity: number) => void
}

const sizeOrder = ['SMALL', 'MEDIUM', 'LARGE']

function displaySize(size: string) {
  return size.charAt(0).toUpperCase() + size.slice(1).toLowerCase()
}

function variantRank(size: string) {
  return sizeOrder.indexOf(size.toUpperCase())
}

export default function MenuItemCard({ items, quantities, onChange }: MenuItemCardProps) {
  const variants = [...items].sort((a, b) => variantRank(a.size) - variantRank(b.size))
  const item = variants[0]
  const totalQuantity = variants.reduce(
    (total, variant) => total + (quantities[variant.id] ?? 0),
    0,
  )
  const totalPrice = variants.reduce(
    (total, variant) => total + variant.price * (quantities[variant.id] ?? 0),
    0,
  )

  if (!item) return null

  return (
    <article className="customer-item-card">
      <div className="customer-item-art">
        {item.imageURL ? (
          <img src={item.imageURL} alt="" />
        ) : (
          <span aria-hidden="true">{menuCategoryArt[item.category] ?? '🥡'}</span>
        )}
        {!item.available && <span className="customer-unavailable">Unavailable</span>}
      </div>
      <div className="customer-item-details">
        <div className="customer-item-title"><strong>{item.name}</strong></div>
        <p className="customer-item-description">Choose quantities by size</p>
        <div className="customer-size-rows">
          {variants.map((variant) => {
            const quantity = quantities[variant.id] ?? 0
            const label = `${displaySize(variant.size)} ${item.name}`
            return (
              <div className="customer-size-row" key={variant.id}>
                <div className="customer-size-info">
                  <strong>{displaySize(variant.size)}</strong>
                  <span>${variant.price.toFixed(2)} each</span>
                </div>
                <div className="customer-quantity" aria-label={`${label} quantity`}>
                  <button
                    type="button"
                    aria-label={`Remove one ${label}`}
                    disabled={!quantity}
                    onClick={() => onChange(variant, quantity - 1)}
                  >
                    −
                  </button>
                  <span aria-live="polite">{quantity}</span>
                  <button
                    type="button"
                    aria-label={`Add one ${label}`}
                    disabled={!variant.available}
                    onClick={() => onChange(variant, quantity + 1)}
                  >
                    +
                  </button>
                </div>
              </div>
            )
          })}
        </div>
        <div className="customer-item-total">
          <span>{totalQuantity} {totalQuantity === 1 ? 'portion' : 'portions'} total</span>
          <strong>${totalPrice.toFixed(2)}</strong>
        </div>
      </div>
    </article>
  )
}
