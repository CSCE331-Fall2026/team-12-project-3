import { useEffect, useMemo, useRef, useState } from 'react'
import { fetchCategoryItems, fetchPopularItems, placeCustomerOrder } from '../api/customerMenu'
import CategoryTabs from '../components/customer/CategoryTabs'
import CheckoutScreen from '../components/customer/CheckoutScreen'
import MenuItemCard from '../components/customer/MenuItemCard'
import OrderSummary from '../components/customer/OrderSummary'
import type { CartLine } from '../components/customer/OrderSummary'
import type { CustomerPaymentMethod, GoogleProfile, MenuCategory, MenuItem } from '../types'
import { customerMenuCategories } from '../types'

type CustomerScreenProps = { profile: GoogleProfile | null }
type MenuData = { popular: MenuItem[]; categories: Record<string, MenuItem[]> }

function groupMenuItems(items: MenuItem[]) {
  const groups = new Map<string, MenuItem[]>()
  items.forEach((item) => {
    const key = `${item.category}:${item.name.trim().toLocaleLowerCase()}`
    groups.set(key, [...(groups.get(key) ?? []), item])
  })
  return [...groups.values()]
}

export default function CustomerScreen({ profile }: CustomerScreenProps) {
  const [step, setStep] = useState<'menu' | 'checkout' | 'complete'>('menu')
  const [activeCategory, setActiveCategory] = useState<MenuCategory>('POPULAR')
  const [menu, setMenu] = useState<MenuData>({ popular: [], categories: {} })
  const [quantities, setQuantities] = useState<Record<number, number>>({})
  const [loading, setLoading] = useState(true)
  const [menuError, setMenuError] = useState('')
  const [orderError, setOrderError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [successNote, setSuccessNote] = useState('')
  const sectionRefs = useRef<Record<string, HTMLElement | null>>({})

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    Promise.allSettled([
      fetchPopularItems(),
      fetchCategoryItems('ENTREE'),
      fetchCategoryItems('SIDE'),
      fetchCategoryItems('APPETIZER'),
      fetchCategoryItems('DRINK'),
    ]).then((results) => {
      if (cancelled) return
      const [popular, entrees, sides, appetizers, drinks] = results
      const categories: Record<string, MenuItem[]> = {}
      const errors: string[] = []
      ;([['ENTREE', entrees], ['SIDE', sides], ['APPETIZER', appetizers], ['DRINK', drinks]] as const).forEach(([key, result]) => {
        if (result.status === 'fulfilled') categories[key] = result.value
        else errors.push(result.reason instanceof Error ? result.reason.message : 'A menu category could not be loaded.')
      })
      if (popular.status === 'fulfilled') setMenu({ popular: popular.value, categories })
      else {
        const detail = popular.reason instanceof Error ? popular.reason.message : 'The popular menu could not be loaded.'
        errors.unshift(detail)
        setMenu({ popular: [], categories })
      }
      setMenuError(errors.length ? errors[0] : '')
      setLoading(false)
    })
    return () => { cancelled = true }
  }, [])

  const lines = useMemo<CartLine[]>(() => {
    const allItems = [...menu.popular, ...Object.values(menu.categories).flat()]
    const unique = new Map<number, MenuItem>()
    allItems.forEach((item) => unique.set(item.id, item))
    return Object.entries(quantities)
      .map(([id, quantity]) => ({ item: unique.get(Number(id)), quantity }))
      .filter((line): line is CartLine => Boolean(line.item) && line.quantity > 0)
  }, [menu, quantities])
  const subtotal = lines.reduce((total, line) => total + line.item.price * line.quantity, 0)

  function changeQuantity(item: MenuItem, quantity: number) {
    setQuantities((current) => {
      const next = { ...current }
      if (quantity <= 0) delete next[item.id]
      else next[item.id] = quantity
      return next
    })
  }

  function selectCategory(category: MenuCategory) {
    setActiveCategory(category)
    if (window.matchMedia('(max-width: 760px)').matches) {
      sectionRefs.current[category]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }

  async function submitOrder(tip: number, method: CustomerPaymentMethod) {
    setSubmitting(true)
    setOrderError('')
    try {
      // JSON object keys are strings; Jackson converts these item ID keys to integers.
      const itemMap: Record<string, number> = Object.fromEntries(
        lines.map(({ item, quantity }) => [String(item.id), quantity]),
      )
      const totalPrice = Math.round((subtotal + tip + Number.EPSILON) * 100) / 100
      await placeCustomerOrder(itemMap, method, totalPrice)
      setSuccessNote(`Your ${method === 'DINING_DOLLARS' ? 'dining dollars' : 'card'} payment was selected.`)
      setStep('complete')
      setQuantities({})
    } catch (error) {
      setOrderError(error instanceof Error ? error.message : 'Your order could not be placed. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  if (step === 'checkout') {
    return <CheckoutScreen lines={lines} subtotal={subtotal} submitting={submitting} error={orderError} onBack={() => setStep('menu')} onPlaceOrder={submitOrder} />
  }

  if (step === 'complete') {
    return (
      <main className="customer-page customer-success-page">
        <section className="customer-success-card">
          <div className="customer-success-icon" aria-hidden="true">✓</div>
          <div className="eyebrow orange">Order received</div>
          <h1>Thanks for your order!</h1>
          <p>Your order has been placed successfully.</p>
          <small>{successNote}</small>
          <button className="customer-primary-button" type="button" onClick={() => setStep('menu')}>Start a new order</button>
        </section>
      </main>
    )
  }

  const visibleItems = activeCategory === 'POPULAR' ? menu.popular : menu.categories[activeCategory] ?? []
  const categoriesWithPopular = customerMenuCategories.map(({ id, label }) => ({
    id,
    label,
    groups: groupMenuItems(id === 'POPULAR' ? menu.popular : menu.categories[id] ?? []),
  }))

  return (
    <main className="customer-page customer-menu-page">
      <div className="customer-menu-heading">
        <div><div className="eyebrow orange">Made fresh for you</div><h1>What sounds good?</h1><p>Pickup at <strong>MSC Food Court</strong><span> · </span>Ready in 12–18 min</p></div>
        <div className="customer-stepper"><b>01&nbsp; Menu</b><i /><span>02&nbsp; Checkout</span></div>
      </div>
      <CategoryTabs category={activeCategory} onSelect={selectCategory} />
      {menuError && <p className="customer-menu-error" role="status">{menuError}</p>}
      {loading ? (
        <div className="customer-loading" role="status">Loading the menu…</div>
      ) : (
        <div className="customer-menu-body">
          <div className="customer-menu-scroll">
            <div className="customer-mobile-sections">
              {categoriesWithPopular.map(({ id, label, groups }) => (
                <section className="customer-menu-section" key={id} ref={(element) => { sectionRefs.current[id] = element }}>
                  <div className="customer-section-heading"><h2>{label}</h2><span>{groups.length} items</span></div>
                  {groups.length ? <div className="customer-item-grid">{groups.map((items) => <MenuItemCard key={`${items[0].category}:${items[0].name}`} items={items} quantities={quantities} onChange={changeQuantity} />)}</div> : <p className="customer-empty-category">No items in this section right now.</p>}
                </section>
              ))}
            </div>
            <div className="customer-desktop-items">
              <div className="customer-section-heading"><h2>{customerMenuCategories.find((entry) => entry.id === activeCategory)?.label}</h2><span>{groupMenuItems(visibleItems).length} items</span></div>
              {visibleItems.length ? <div className="customer-item-grid">{groupMenuItems(visibleItems).map((items) => <MenuItemCard key={`${items[0].category}:${items[0].name}`} items={items} quantities={quantities} onChange={changeQuantity} />)}</div> : <p className="customer-empty-category">No items in this section right now.</p>}
            </div>
          </div>
          <div className="customer-desktop-summary"><OrderSummary lines={lines} subtotal={subtotal} onCheckout={() => { setOrderError(''); setStep('checkout') }} /></div>
        </div>
      )}
      <div className="customer-mobile-summary"><OrderSummary lines={lines} subtotal={subtotal} onCheckout={() => { setOrderError(''); setStep('checkout') }} mobile /></div>
      <span className="sr-only">Signed in as {profile?.email}</span>
    </main>
  )
}
