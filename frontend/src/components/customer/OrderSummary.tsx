import type { MenuItem } from '../../types'

export type CartLine = { item: MenuItem; quantity: number }

type OrderSummaryProps = {
  lines: CartLine[]
  subtotal: number
  onCheckout: () => void
  mobile?: boolean
}

function formatCurrency(value: number) {
  return `$${value.toFixed(2)}`
}

export default function OrderSummary({
  lines,
  subtotal,
  onCheckout,
  mobile = false,
}: OrderSummaryProps) {
  const itemCount = lines.reduce((total, line) => total + line.quantity, 0)

  return (
    <aside
      className={`customer-order-summary ${mobile ? 'customer-order-summary-mobile' : ''}`}
      aria-label="Current order"
    >
      {mobile ? (
        <>
          <div className="customer-mobile-total">
            <strong>{itemCount} {itemCount === 1 ? 'item' : 'items'}</strong>
            <span>Subtotal <b>{formatCurrency(subtotal)}</b></span>
          </div>
          <button
            className="customer-primary-button"
            type="button"
            disabled={!itemCount}
            onClick={onCheckout}
          >
            View order <span aria-hidden="true">→</span>
          </button>
        </>
      ) : (
        <>
          <div className="customer-summary-heading">
            <strong>Your order</strong>
            <span>{itemCount} {itemCount === 1 ? 'ITEM' : 'ITEMS'}</span>
          </div>
          {lines.length ? (
            <ul className="customer-cart-lines">
              {lines.map(({ item, quantity }) => (
                <li key={item.id}>
                  <span>
                    <b>{item.name}</b>
                    <small>
                      {item.size.toLowerCase()} · Qty {quantity} ·{' '}
                      {formatCurrency(item.price)} each
                    </small>
                  </span>
                  <strong>{formatCurrency(item.price * quantity)}</strong>
                </li>
              ))}
            </ul>
          ) : (
            <p className="customer-cart-empty">
              Your order is empty.<br />Add something delicious to get started.
            </p>
          )}
          <div className="customer-subtotal">
            <span>Subtotal</span>
            <strong>{formatCurrency(subtotal)}</strong>
          </div>
          <button
            className="customer-primary-button"
            type="button"
            disabled={!itemCount}
            onClick={onCheckout}
          >
            Continue to checkout <span aria-hidden="true">→</span>
          </button>
          <p className="customer-summary-note">Choose your tip and payment at checkout.</p>
        </>
      )}
    </aside>
  )
}
