import { useEffect, useState, type FormEvent } from 'react'

import { fetchSalesReport, type SalesReportData } from '../api'

type DateRange = { start: string; end: string }
type DailySales = { date: string; revenue: number }
type ItemSales = { name: string; revenue: number }

function toDateInputValue(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function defaultDateRange(): DateRange {
  const end = new Date()
  const start = new Date(end)
  start.setDate(start.getDate() - 6)
  return { start: toDateInputValue(start), end: toDateInputValue(end) }
}

function datesInRange(range: DateRange): string[] {
  const dates: string[] = []
  const current = new Date(`${range.start}T00:00:00`)
  const end = new Date(`${range.end}T00:00:00`)
  while (current <= end) {
    dates.push(toDateInputValue(current))
    current.setDate(current.getDate() + 1)
  }
  return dates
}

function currency(value: number) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value)
}

function shortDate(value: string) {
  return new Date(`${value}T00:00:00`).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
  })
}

function buildItemSales(report: SalesReportData): ItemSales[] {
  const totals: Record<string, number> = {}
  Object.values(report).forEach((items) => {
    Object.entries(items).forEach(([name, revenue]) => {
      totals[name] = (totals[name] ?? 0) + revenue
    })
  })
  return Object.entries(totals)
    .map(([name, revenue]) => ({ name, revenue }))
    .sort((a, b) => b.revenue - a.revenue)
}

function SalesTrendChart({ dailySales }: { dailySales: DailySales[] }) {
  const chart = { left: 62, right: 900, top: 24, bottom: 250 }
  const maxRevenue = Math.max(...dailySales.map(({ revenue }) => revenue), 0)
  const maxY = maxRevenue > 0 ? Math.ceil(maxRevenue / 4) * 4 : 4
  const points = dailySales.map(({ revenue }, index) => {
    const x = chart.left + index * ((chart.right - chart.left) / Math.max(dailySales.length - 1, 1))
    const y = chart.bottom - (revenue / maxY) * (chart.bottom - chart.top)
    return { x, y }
  })
  const dateLabels = [
    { date: dailySales[0]?.date, x: chart.left },
    { date: dailySales[Math.floor((dailySales.length - 1) / 2)]?.date, x: (chart.left + chart.right) / 2 },
    { date: dailySales[dailySales.length - 1]?.date, x: chart.right },
  ]

  return (
    <div className="sales-chart-scroll">
      <svg
        className="sales-chart"
        viewBox="0 0 940 310"
        role="img"
        aria-label="Daily sales revenue trend"
      >
        <text className="sales-axis-title" x="14" y="18">Revenue (USD)</text>
        {[0, 1, 2, 3, 4].map((step) => {
          const value = (maxY / 4) * (4 - step)
          const y = chart.top + step * ((chart.bottom - chart.top) / 4)
          return (
            <g key={step}>
              <line className="sales-grid-line" x1={chart.left} y1={y} x2={chart.right} y2={y} />
              <text className="sales-axis-label" x={chart.left - 10} y={y + 4} textAnchor="end">
                {currency(value)}
              </text>
            </g>
          )
        })}
        <line className="sales-axis-line" x1={chart.left} y1={chart.bottom} x2={chart.right} y2={chart.bottom} />
        {points.length > 1 && (
          <polyline
            className="sales-trend-line"
            points={points.map(({ x, y }) => `${x},${y}`).join(' ')}
          />
        )}
        {points.map(({ x, y }, index) => (
          <circle className="sales-trend-point" key={dailySales[index].date} cx={x} cy={y} r="4" />
        ))}
        {dateLabels.map(({ date, x }, index) => date && (
          <text
            className="sales-axis-label"
            key={`${date}-${index}`}
            x={x}
            y={chart.bottom + 22}
            textAnchor={index === 0 ? 'start' : index === 2 ? 'end' : 'middle'}
          >
            {shortDate(date)}
          </text>
        ))}
        <text className="sales-axis-title" x={(chart.left + chart.right) / 2} y="300" textAnchor="middle">
          Date
        </text>
      </svg>
    </div>
  )
}

export default function SalesReport() {
  const [dates, setDates] = useState<DateRange>(defaultDateRange)
  const [loadedRange, setLoadedRange] = useState<DateRange>(defaultDateRange)
  const [report, setReport] = useState<SalesReportData>({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    fetchSalesReport(loadedRange.start, loadedRange.end)
      .then((data) => {
        if (!active) return
        setReport(data)
        setError('')
      })
      .catch((cause: unknown) => {
        if (active) setError(cause instanceof Error ? cause.message : 'Sales report could not be loaded.')
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [loadedRange])

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (dates.start > dates.end) {
      setError('The start date must be on or before the end date.')
      return
    }
    setError('')
    setLoading(true)
    setReport({})
    setLoadedRange({ ...dates })
  }

  const dailySales = datesInRange(loadedRange).map((date) => ({
    date,
    revenue: Object.values(report[date] ?? {}).reduce((total, value) => total + value, 0),
  }))
  const itemSales = buildItemSales(report)
  const totalRevenue = dailySales.reduce((total, day) => total + day.revenue, 0)

  return (
    <section className="sales-report" aria-labelledby="sales-report-title">
      <div className="sales-report-heading">
        <div>
          <h2 id="sales-report-title">Sales report</h2>
          <p className="muted">Track daily revenue and see which menu items are selling.</p>
        </div>
        <form className="sales-date-form" onSubmit={handleSubmit}>
          <label>
            Start date
            <input
              type="date"
              value={dates.start}
              onChange={(event) => setDates((current) => ({ ...current, start: event.target.value }))}
              required
            />
          </label>
          <label>
            End date
            <input
              type="date"
              value={dates.end}
              onChange={(event) => setDates((current) => ({ ...current, end: event.target.value }))}
              required
            />
          </label>
          <button type="submit" disabled={loading}>Update report</button>
        </form>
      </div>
      {error && <p className="error-message" role="alert">{error}</p>}
      {loading ? (
        <p className="muted" role="status">Loading sales report…</p>
      ) : (
        <>
          <div className="sales-summary">
            <span className="sales-summary-label">Total revenue</span>
            <strong>{currency(totalRevenue)}</strong>
            <span className="muted">{shortDate(loadedRange.start)} – {shortDate(loadedRange.end)}</span>
          </div>
          <div className="sales-report-grid">
            <section className="sales-card" aria-labelledby="sales-trend-title">
              <h3 id="sales-trend-title">Daily sales trend</h3>
              {dailySales.length > 0
                ? <SalesTrendChart dailySales={dailySales} />
                : <p className="muted">Select a valid date range to view sales.</p>}
            </section>
            <section className="sales-card" aria-labelledby="top-items-title">
              <h3 id="top-items-title">Top-selling items</h3>
              {itemSales.length ? (
                <ol className="sales-item-list">
                  {itemSales.slice(0, 10).map(({ name, revenue }) => (
                    <li key={name}><span>{name}</span><strong>{currency(revenue)}</strong></li>
                  ))}
                </ol>
              ) : (
                <p className="muted">No sales in this date range.</p>
              )}
            </section>
          </div>
        </>
      )}
    </section>
  )
}
