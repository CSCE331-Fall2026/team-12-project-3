import type { MenuItem } from '../../types'

type MenuItemCardProps = {
  items: MenuItem[]
  quantities: Record<number, number>
  onChange: (item: MenuItem, quantity: number) => void
}

const fallbackArt: Record<string, string> = {
  ENTREE: '🥡',
  SIDE: '🍜',
  APPETIZER: '🥟',
  DRINK: '🥤',
}

const sizeOrder = ['SMALL', 'MEDIUM', 'LARGE']

function displaySize(size: string) {
  return size.charAt(0).toUpperCase() + size.slice(1).toLowerCase()
}

export default function MenuItemCard({ items, quantities, onChange }: MenuItemCardProps) {
  const variants = [...items].sort((a, b) => sizeOrder.indexOf(a.size.toUpperCase()) - sizeOrder.indexOf(b.size.toUpperCase()))
  const item = variants[0]
  const totalQuantity = variants.reduce((total, variant) => total + (quantities[variant.id] ?? 0), 0)
  const totalPrice = variants.reduce((total, variant) => total + variant.price * (quantities[variant.id] ?? 0), 0)

  if (!item) return null

  return (
    <article className="customer-item-card">
      <div className="customer-item-art">
        {item.imageURL
          ? <img src={item.imageURL} alt="" />
          : <span aria-hidden="true">{fallbackArt[item.category] ?? '🥡'}</span>}
        {!item.available && <span className="customer-unavailable">Unavailable</span>}
      </div>
      <div className="customer-item-details">
        <div className="customer-item-title"><strong>{item.name}</strong></div>
        <p className="customer-item-description">Choose quantities by size</p>
        <div className="customer-size-rows">
          {variants.map((variant) => {
            const quantity = quantities[variant.id] ?? 0
            return (
              <div className="customer-size-row" key={variant.id}>
                <div className="customer-size-info"><strong>{displaySize(variant.size)}</strong><span>${variant.price.toFixed(2)} each</span></div>
                <div className="customer-quantity" aria-label={`${displaySize(variant.size)} ${item.name} quantity`}>
                  <button type="button" aria-label={`Remove one ${displaySize(variant.size)} ${item.name}`} disabled={!quantity} onClick={() => onChange(variant, quantity - 1)}>−</button>
                  <span aria-live="polite">{quantity}</span>
                  <button type="button" aria-label={`Add one ${displaySize(variant.size)} ${item.name}`} disabled={!variant.available} onClick={() => onChange(variant, quantity + 1)}>+</button>
                </div>
              </div>
            )
          })}
        </div>
        <div className="customer-item-total"><span>{totalQuantity} {totalQuantity === 1 ? 'portion' : 'portions'} total</span><strong>${totalPrice.toFixed(2)}</strong></div>
      </div>
    </article>
  )
}
