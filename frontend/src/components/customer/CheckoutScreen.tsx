import { useState } from 'react'
import type { CartLine } from './OrderSummary'
import type { CustomerPaymentMethod } from '../../types'

type CheckoutScreenProps = {
  lines: CartLine[]
  subtotal: number
  submitting: boolean
  error: string
  onBack: () => void
  onPlaceOrder: (tip: number, method: CustomerPaymentMethod) => void
}

const tipOptions = [0, 0.1, 0.15, 0.2]

export default function CheckoutScreen({ lines, subtotal, submitting, error, onBack, onPlaceOrder }: CheckoutScreenProps) {
  const [tipPercent, setTipPercent] = useState(0.15)
  const [customTip, setCustomTip] = useState('')
  const [customSelected, setCustomSelected] = useState(false)
  const [method, setMethod] = useState<CustomerPaymentMethod>('CREDIT_CARD')
  const tip = customSelected ? Math.max(0, Number(customTip) || 0) : subtotal * tipPercent
  const total = subtotal + tip
  const itemCount = lines.reduce((count, line) => count + line.quantity, 0)
  const currency = (value: number) => `$${value.toFixed(2)}`

  return (
    <main className="customer-page customer-checkout-page">
      <div className="customer-checkout-heading">
        <div><div className="eyebrow orange">Almost there</div><h1>Review &amp; pay</h1><p className="muted">Your favorites, ready to go.</p></div>
        <div className="customer-stepper"><span>01&nbsp; Menu</span><i /><b>02&nbsp; Checkout</b></div>
      </div>
      <div className="customer-checkout-grid">
        <section className="customer-panel">
          <h2>Your order <span className="customer-count-pill">{itemCount} ITEMS</span></h2>
          <ul className="customer-review-lines">
            {lines.map(({ item, quantity }) => (
              <li key={item.id}><div className="customer-review-art">{item.imageURL ? <img src={item.imageURL} alt="" /> : <span>{item.category === 'DRINK' ? '🥤' : item.category === 'APPETIZER' ? '🥟' : '🥡'}</span>}</div><span><b>{item.name}</b><small>{item.size.toLowerCase()} · Quantity {quantity}</small></span><strong>{currency(item.price * quantity)}</strong></li>
            ))}
          </ul>
          <div className="customer-cost-lines"><div><span>Subtotal</span><b>{currency(subtotal)}</b></div><div><span>Tip</span><b>{currency(tip)}</b></div><div className="customer-grand-total"><span>Total</span><strong>{currency(total)}</strong></div></div>
        </section>
        <section className="customer-panel customer-payment-panel">
          <h2>Add a tip</h2>
          <div className="customer-tip-options">
            {tipOptions.map((percent) => (
              <label className={`customer-tip-option ${!customSelected && tipPercent === percent ? 'selected' : ''}`} key={percent}>
                <input type="radio" name="tip" checked={!customSelected && tipPercent === percent} onChange={() => { setCustomSelected(false); setTipPercent(percent) }} />
                <strong>{percent === 0 ? 'No tip' : `${percent * 100}%`}</strong><small>{currency(subtotal * percent)}</small>
              </label>
            ))}
            <label className={`customer-tip-option ${customSelected ? 'selected' : ''}`}>
              <input type="radio" name="tip" checked={customSelected} onChange={() => setCustomSelected(true)} />
              <strong>Custom</strong><small>Enter amount</small>
            </label>
          </div>
          {customSelected && <label className="customer-custom-tip">Custom tip amount<input type="number" min="0" step="0.01" value={customTip} onChange={(event) => setCustomTip(event.target.value)} placeholder="0.00" /></label>}
          <div className="customer-payment-heading">Payment method</div>
          <label className={`customer-payment-choice ${method === 'CREDIT_CARD' ? 'selected' : ''}`}><input type="radio" name="payment" checked={method === 'CREDIT_CARD'} onChange={() => setMethod('CREDIT_CARD')} /><span><b>Credit or debit card</b><small>•••• &nbsp;4242</small></span></label>
          <label className={`customer-payment-choice ${method === 'DINING_DOLLARS' ? 'selected' : ''}`}><input type="radio" name="payment" checked={method === 'DINING_DOLLARS'} onChange={() => setMethod('DINING_DOLLARS')} /><span><b>Dining dollars</b><small>Campus account</small></span></label>
          {error && <p className="customer-action-error" role="alert">{error}</p>}
          <button className="customer-primary-button customer-place-button" type="button" disabled={submitting} onClick={() => onPlaceOrder(tip, method)}>{submitting ? 'Placing order…' : `Place order · ${currency(total)}`}</button>
          <button className="customer-back-button" type="button" onClick={onBack}>← Back to menu</button>
        </section>
      </div>
    </main>
  )
}
