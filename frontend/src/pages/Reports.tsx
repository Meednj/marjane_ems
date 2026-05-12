import { useEffect, useMemo, useState } from "react";
import Navbar from "../components/Navbar";
import { getRole } from "../api/auth";
import { fetchReportMetrics, type ReportMetrics } from "../api/reportService";

const emptyMetrics: ReportMetrics = {
  usersCount: 0,
  ticketsCount: 0,
  pendingTickets: 0,
  departmentsCount: 0,
  totalLeaves: 0,
  pendingLeaves: 0,
  resolvedTickets: 0,
};

const Reports = () => {
  const [metrics, setMetrics] = useState<ReportMetrics>(emptyMetrics);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);

  useEffect(() => {
    const loadMetrics = async () => {
      setLoading(true);
      setError(null);

      try {
        setUserRole(getRole());
        const data = await fetchReportMetrics();
        setMetrics(data);
      } catch (err) {
        console.error("Error loading reports:", err);
        setError("Failed to load analytics reports.");
      } finally {
        setLoading(false);
      }
    };

    loadMetrics();
  }, []);

  const resolutionRate = useMemo(() => {
    if (!metrics.ticketsCount) {
      return 0;
    }

    return Math.round((metrics.resolvedTickets / metrics.ticketsCount) * 100);
  }, [metrics]);

  const leavePressure = useMemo(() => {
    if (!metrics.totalLeaves) {
      return 0;
    }

    return Math.round((metrics.pendingLeaves / metrics.totalLeaves) * 100);
  }, [metrics]);

  const activityBands = useMemo(() => {
    const normalized = [
      {
        label: "Users",
        value: metrics.usersCount,
        color: "from-sky-500 to-cyan-400",
      },
      {
        label: "Tickets",
        value: metrics.ticketsCount,
        color: "from-indigo-500 to-violet-400",
      },
      {
        label: "Departments",
        value: metrics.departmentsCount,
        color: "from-emerald-500 to-lime-400",
      },
      {
        label: "Leaves",
        value: metrics.totalLeaves,
        color: "from-rose-500 to-orange-400",
      },
    ];

    const max = Math.max(...normalized.map((item) => item.value), 1);

    return normalized.map((item) => ({
      ...item,
      width: Math.max(8, Math.round((item.value / max) * 100)),
    }));
  }, [metrics]);

  const ticketStatusSlices = useMemo(
    () => [
      {
        label: "Resolved",
        value: metrics.resolvedTickets,
        color: "#10b981",
      },
      {
        label: "Pending",
        value: metrics.pendingTickets,
        color: "#f59e0b",
      },
      {
        label: "Other",
        value: Math.max(
          metrics.ticketsCount -
            metrics.resolvedTickets -
            metrics.pendingTickets,
          0,
        ),
        color: "#6366f1",
      },
    ],
    [metrics],
  );

  const leaveStatusSlices = useMemo(
    () => [
      {
        label: "Pending",
        value: metrics.pendingLeaves,
        color: "#fb7185",
      },
      {
        label: "Processed",
        value: Math.max(metrics.totalLeaves - metrics.pendingLeaves, 0),
        color: "#38bdf8",
      },
    ],
    [metrics],
  );

  const sectionTitle =
    "text-sm font-semibold uppercase tracking-[0.25em] text-slate-400";

  const cards = [
    { label: "Total Users", value: metrics.usersCount, color: "text-blue-600" },
    {
      label: "Total Tickets",
      value: metrics.ticketsCount,
      color: "text-indigo-600",
    },
    {
      label: "Pending Tickets",
      value: metrics.pendingTickets,
      color: "text-amber-600",
    },
    {
      label: "Resolved Tickets",
      value: metrics.resolvedTickets,
      color: "text-emerald-600",
    },
    {
      label: "Departments",
      value: metrics.departmentsCount,
      color: "text-cyan-600",
    },
    {
      label: "Total Leaves",
      value: metrics.totalLeaves,
      color: "text-purple-600",
    },
    {
      label: "Pending Leaves",
      value: metrics.pendingLeaves,
      color: "text-rose-600",
    },
    {
      label: "Resolution Rate",
      value: `${resolutionRate}%`,
      color: "text-slate-700",
    },
  ];

  return (
    <div className="min-h-screen bg-linear-to-br from-slate-900 via-slate-800 to-slate-900">
      <Navbar
        userRole={userRole}
        title="Reports & Analytics"
        subtitle="Operational metrics overview"
      />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {error && (
          <div className="mb-6 p-5 bg-yellow-50 border-2 border-yellow-400 rounded-lg text-yellow-800">
            {error}
          </div>
        )}

        {loading ? (
          <div className="text-white text-center py-12">
            Loading analytics...
          </div>
        ) : (
          <div className="space-y-6">
            <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <div className="lg:col-span-2 rounded-3xl bg-white/95 shadow-[0_24px_80px_rgba(15,23,42,0.22)] border border-white/40 overflow-hidden">
                <div className="p-6 sm:p-8 border-b border-slate-200/80">
                  <p className={sectionTitle}>Live Overview</p>
                  <h2 className="mt-2 text-2xl sm:text-3xl font-black text-slate-900">
                    System activity at a glance
                  </h2>
                  <p className="mt-2 text-slate-600 max-w-2xl">
                    A compact operations board showing how work is moving across
                    users, tickets, departments, and leave requests.
                  </p>
                </div>

                <div className="p-6 sm:p-8 grid grid-cols-1 md:grid-cols-2 gap-4">
                  {cards.map((card) => (
                    <div
                      key={card.label}
                      className="rounded-2xl border border-slate-200 bg-slate-50/80 p-5 shadow-sm"
                    >
                      <p className="text-xs font-semibold uppercase tracking-[0.25em] text-slate-500">
                        {card.label}
                      </p>
                      <p className={`mt-3 text-4xl font-black ${card.color}`}>
                        {card.value}
                      </p>
                    </div>
                  ))}
                </div>
              </div>

              <div className="rounded-3xl bg-slate-950 text-white shadow-[0_24px_80px_rgba(15,23,42,0.45)] overflow-hidden border border-slate-800">
                <div className="p-6 sm:p-8 border-b border-white/10">
                  <p className="text-xs font-semibold uppercase tracking-[0.25em] text-slate-400">
                    Health Indicators
                  </p>
                  <h3 className="mt-2 text-2xl font-black">Fast signals</h3>
                </div>

                <div className="p-6 sm:p-8 space-y-5">
                  <div>
                    <div className="flex items-center justify-between text-sm text-slate-300 mb-2">
                      <span>Ticket resolution</span>
                      <span className="font-semibold text-white">
                        {resolutionRate}%
                      </span>
                    </div>
                    <div className="h-3 rounded-full bg-white/10 overflow-hidden">
                      <div
                        className="h-full rounded-full bg-linear-to-r from-emerald-400 via-cyan-400 to-sky-500"
                        style={{ width: `${resolutionRate}%` }}
                      />
                    </div>
                  </div>

                  <div>
                    <div className="flex items-center justify-between text-sm text-slate-300 mb-2">
                      <span>Leave pressure</span>
                      <span className="font-semibold text-white">
                        {leavePressure}%
                      </span>
                    </div>
                    <div className="h-3 rounded-full bg-white/10 overflow-hidden">
                      <div
                        className="h-full rounded-full bg-linear-to-r from-rose-400 via-orange-400 to-amber-400"
                        style={{ width: `${leavePressure}%` }}
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4 pt-2">
                    <div className="rounded-2xl bg-white/5 p-4">
                      <p className="text-xs uppercase tracking-[0.25em] text-slate-400">
                        Resolved tickets
                      </p>
                      <p className="mt-2 text-3xl font-black text-emerald-300">
                        {metrics.resolvedTickets}
                      </p>
                    </div>
                    <div className="rounded-2xl bg-white/5 p-4">
                      <p className="text-xs uppercase tracking-[0.25em] text-slate-400">
                        Pending leaves
                      </p>
                      <p className="mt-2 text-3xl font-black text-rose-300">
                        {metrics.pendingLeaves}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </section>

            <section className="grid grid-cols-1 xl:grid-cols-3 gap-6">
              <div className="xl:col-span-2 rounded-3xl bg-white/95 shadow-[0_24px_80px_rgba(15,23,42,0.18)] border border-white/40 p-6 sm:p-8">
                <p className={sectionTitle}>Activity Flow</p>
                <h3 className="mt-2 text-2xl font-black text-slate-900">
                  Department-wide load
                </h3>
                <div className="mt-6 space-y-4">
                  {activityBands.map((band) => (
                    <div key={band.label} className="space-y-2">
                      <div className="flex items-center justify-between text-sm text-slate-600">
                        <span className="font-semibold text-slate-800">
                          {band.label}
                        </span>
                        <span>{band.value}</span>
                      </div>
                      <div className="h-4 rounded-full bg-slate-100 overflow-hidden">
                        <div
                          className={`h-full rounded-full bg-linear-to-r ${band.color}`}
                          style={{ width: `${band.width}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="rounded-3xl bg-white/95 shadow-[0_24px_80px_rgba(15,23,42,0.18)] border border-white/40 p-6 sm:p-8">
                <p className={sectionTitle}>Ticket Status</p>
                <h3 className="mt-2 text-2xl font-black text-slate-900">
                  Resolution mix
                </h3>

                <DonutChart
                  slices={ticketStatusSlices}
                  centerLabel="Tickets"
                  centerValue={metrics.ticketsCount}
                />

                <Legend slices={ticketStatusSlices} />
              </div>
            </section>

            <section className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div className="rounded-3xl bg-white/95 shadow-[0_24px_80px_rgba(15,23,42,0.18)] border border-white/40 p-6 sm:p-8">
                <p className={sectionTitle}>Leave Status</p>
                <h3 className="mt-2 text-2xl font-black text-slate-900">
                  Pending vs processed
                </h3>

                <DonutChart
                  slices={leaveStatusSlices}
                  centerLabel="Leaves"
                  centerValue={metrics.totalLeaves}
                />

                <Legend slices={leaveStatusSlices} />
              </div>

              <div className="rounded-3xl bg-slate-950 text-white shadow-[0_24px_80px_rgba(15,23,42,0.45)] overflow-hidden border border-slate-800">
                <div className="p-6 sm:p-8 border-b border-white/10">
                  <p className="text-xs font-semibold uppercase tracking-[0.25em] text-slate-400">
                    Executive Snapshot
                  </p>
                  <h3 className="mt-2 text-2xl font-black">
                    Operational summary
                  </h3>
                </div>
                <div className="p-6 sm:p-8 grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <MetricCard
                    label="Users"
                    value={metrics.usersCount}
                    tone="from-sky-500 to-cyan-400"
                  />
                  <MetricCard
                    label="Departments"
                    value={metrics.departmentsCount}
                    tone="from-emerald-500 to-lime-400"
                  />
                  <MetricCard
                    label="Open tickets"
                    value={Math.max(
                      metrics.ticketsCount - metrics.resolvedTickets,
                      0,
                    )}
                    tone="from-amber-500 to-orange-400"
                  />
                  <MetricCard
                    label="Pending leaves"
                    value={metrics.pendingLeaves}
                    tone="from-rose-500 to-pink-400"
                  />
                </div>
              </div>
            </section>
          </div>
        )}
      </main>
    </div>
  );
};

type Slice = {
  label: string;
  value: number;
  color: string;
};

const DonutChart = ({
  slices,
  centerLabel,
  centerValue,
}: {
  slices: Slice[];
  centerLabel: string;
  centerValue: number;
}) => {
  const size = 220;
  const strokeWidth = 24;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const total = slices.reduce((sum, slice) => sum + slice.value, 0) || 1;

  let accumulated = 0;

  return (
    <div className="mt-6 flex flex-col items-center">
      <svg viewBox={`0 0 ${size} ${size}`} className="w-56 h-56">
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="#e2e8f0"
          strokeWidth={strokeWidth}
        />
        {slices.map((slice) => {
          const dashArray = `${(slice.value / total) * circumference} ${circumference}`;
          const dashOffset =
            circumference - accumulated - (slice.value / total) * circumference;
          accumulated += (slice.value / total) * circumference;

          return (
            <circle
              key={slice.label}
              cx={size / 2}
              cy={size / 2}
              r={radius}
              fill="none"
              stroke={slice.color}
              strokeWidth={strokeWidth}
              strokeDasharray={dashArray}
              strokeDashoffset={dashOffset}
              strokeLinecap="round"
              transform={`rotate(-90 ${size / 2} ${size / 2})`}
            />
          );
        })}
      </svg>

      <div className="-mt-44 flex h-44 w-44 flex-col items-center justify-center rounded-full bg-white shadow-inner ring-8 ring-slate-100">
        <p className="text-xs font-semibold uppercase tracking-[0.25em] text-slate-400">
          {centerLabel}
        </p>
        <p className="mt-1 text-4xl font-black text-slate-900">{centerValue}</p>
      </div>
    </div>
  );
};

const Legend = ({ slices }: { slices: Slice[] }) => {
  return (
    <div className="mt-6 space-y-3">
      {slices.map((slice) => (
        <div
          key={slice.label}
          className="flex items-center justify-between text-sm"
        >
          <div className="flex items-center gap-3">
            <span
              className="h-3 w-3 rounded-full"
              style={{ backgroundColor: slice.color }}
            />
            <span className="font-semibold text-slate-700">{slice.label}</span>
          </div>
          <span className="font-black text-slate-900">{slice.value}</span>
        </div>
      ))}
    </div>
  );
};

const MetricCard = ({
  label,
  value,
  tone,
}: {
  label: string;
  value: number;
  tone: string;
}) => {
  return (
    <div className="rounded-2xl bg-white/5 p-4 ring-1 ring-white/10">
      <div className={`h-1.5 w-20 rounded-full bg-linear-to-r ${tone}`} />
      <p className="mt-4 text-xs uppercase tracking-[0.25em] text-slate-400">
        {label}
      </p>
      <p className="mt-2 text-3xl font-black text-white">{value}</p>
    </div>
  );
};

export default Reports;
