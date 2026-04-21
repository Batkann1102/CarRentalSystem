import { useMemo, useState } from 'react';
import { CarFront, LayoutDashboard, ListChecks, MoonStar, PlusCircle, SunMedium, X } from 'lucide-react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import CarForm from './CarForm';
import { EMPTY_CAR_FORM } from '../constants';
import { api } from '../api';
import { mapErrorMessage, normalizeCarPayload } from '../utils';

function AppShell({ theme, onToggleTheme, onNotify }) {
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [createForm, setCreateForm] = useState(EMPTY_CAR_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [carsRefreshKey, setCarsRefreshKey] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();

  const outletContext = useMemo(() => ({ carsRefreshKey }), [carsRefreshKey]);

  function handleCreateFormChange(field, value) {
    setCreateForm((current) => ({ ...current, [field]: value }));
  }

  async function handleCreateSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await api.createCar(normalizeCarPayload(createForm));
      onNotify('success', 'Шинэ машин амжилттай нэмэгдлээ.');
      setCreateForm(EMPTY_CAR_FORM);
      setShowCreateForm(false);
      setCarsRefreshKey((current) => current + 1);
      if (location.pathname !== '/cars') {
        navigate('/cars');
      }
    } catch (error) {
      onNotify('error', mapErrorMessage(error.message));
    } finally {
      setSubmitting(false);
    }
  }

  function handleToggleCreateForm() {
    setShowCreateForm((current) => !current);
    if (location.pathname !== '/cars') {
      navigate('/cars');
    }
  }

  return (
    <div className="page-shell">
      <div className="background-orb orb-one" />
      <div className="background-orb orb-two" />
      <div className="page app-layout">
        <aside className="sidebar card-panel">
          <div className="brand-block">
            <div className="brand-icon">
              <CarFront size={20} />
            </div>
            <div>
              <p className="eyebrow">Car Rental</p>
              <h1>Машин түрээс</h1>
            </div>
          </div>

          <nav className="nav-stack">
            <NavLink to="/" end className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <LayoutDashboard size={18} />
              <span>Хянах самбар</span>
            </NavLink>
            <NavLink to="/cars" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <ListChecks size={18} />
              <span>Машины удирдлага</span>
            </NavLink>
            <button
              type="button"
              className={`nav-link sidebar-create-toggle ${showCreateForm ? 'active' : ''}`}
              onClick={handleToggleCreateForm}
              aria-expanded={showCreateForm}
              aria-controls="sidebar-create-car-form"
            >
              {showCreateForm ? <X size={18} /> : <PlusCircle size={18} />}
              <span>{showCreateForm ? 'Шинэ машин хаах' : 'Шинэ машин'}</span>
            </button>
          </nav>

          <div className={`sidebar-create-shell ${showCreateForm ? 'open' : ''}`}>
            <div className="sidebar-create-inner">
              <section id="sidebar-create-car-form" className="nested-panel sidebar-create-panel">
                <div className="compact">
                  <p className="section-label">Create</p>
                  <strong>Шинэ машин нэмэх</strong>
                  <p className="subtle">Шинэ машин нэмэх форм sidebar дээрээс шууд ажиллана.</p>
                </div>
                <CarForm
                  form={createForm}
                  onChange={handleCreateFormChange}
                  onSubmit={handleCreateSubmit}
                  submitLabel="Машин нэмэх"
                  submitting={submitting}
                  compact
                />
              </section>
            </div>
          </div>

          <button type="button" className="icon-button theme-button" onClick={onToggleTheme}>
            {theme === 'dark' ? <SunMedium size={18} /> : <MoonStar size={18} />}
            <span>{theme === 'dark' ? 'Гэрэлт' : 'Харанхуй'} горим</span>
          </button>
        </aside>

        <main className="content-stack">
          <Outlet context={outletContext} />
        </main>
      </div>
    </div>
  );
}

export default AppShell;

