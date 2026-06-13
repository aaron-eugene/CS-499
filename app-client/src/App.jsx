import { useCallback, useEffect, useRef, useState } from 'react';
import { getTrips } from './api/tripApi';
import AdminTripManager from './components/admin/AdminTripManager';
import TripList from './components/TripList';
import './styles/index.css';

/**
 * Main application component for the enhanced Travlr client.
 *
 * This component retrieves trip data from the Spring Boot API, displays the
 * public trip-browsing experience, and switches to a separate admin management
 * view when requested.
 *
 * @returns {JSX.Element} rendered application
 */
function App() {
  const [view, setView] = useState('public');
  const [publicTrips, setPublicTrips] = useState([]);
  const [adminTrips, setAdminTrips] = useState([]);
  const [sort, setSort] = useState('name');
  const [maxPrice, setMaxPrice] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  const isMounted = useRef(false);
  const latestRequestId = useRef(0);

  useEffect(() => {
    isMounted.current = true;

    return () => {
      isMounted.current = false;
      latestRequestId.current += 1;
    };
  }, []);

  /**
   * Loads filtered trips for the public browsing page.
   */
  const loadPublicTrips = useCallback(async () => {
    const requestId = latestRequestId.current + 1;
    latestRequestId.current = requestId;

    try {
      setIsLoading(true);
      setErrorMessage('');

      const tripData = await getTrips({ sort, maxPrice });

      if (isMounted.current && latestRequestId.current === requestId) {
        setPublicTrips(tripData);
      }
    } catch (error) {
      if (isMounted.current && latestRequestId.current === requestId) {
        setErrorMessage(error.message);
        setPublicTrips([]);
      }
    } finally {
      if (isMounted.current && latestRequestId.current === requestId) {
        setIsLoading(false);
      }
    }
  }, [sort, maxPrice]);

  /**
   * Loads the full trip catalog for admin management.
   */
  const loadAdminTrips = useCallback(async () => {
    try {
      const tripData = await getTrips({ sort: 'name' });

      if (isMounted.current) {
        setAdminTrips(tripData);
      }
    } catch (error) {
      if (isMounted.current) {
        setAdminTrips([]);
      }
    }
  }, []);

  /**
   * Refreshes both public and admin trip data after an admin change.
   */
  async function handleTripsChanged() {
    await Promise.all([
      loadPublicTrips(),
      loadAdminTrips(),
    ]);
  }

  useEffect(() => {
    loadPublicTrips();
  }, [loadPublicTrips]);

  useEffect(() => {
    loadAdminTrips();
  }, [loadAdminTrips]);

  return (
    <main className="app-shell">
      <header className="site-header">
        <div className="site-header__inner">
          <div className="site-header__top">
            <img
              className="site-logo"
              src="/images/logo.png"
              alt="Travlr Getaways"
            />

            <button
              type="button"
              className="admin-link-button"
              onClick={() => setView('admin')}
            >
              Admin
            </button>
          </div>

          <nav className="site-nav" aria-label="Primary navigation">
            <button type="button" onClick={() => setView('public')}>
              Home
            </button>
            <button
              type="button"
              className={view === 'public' ? 'site-nav__active' : ''}
              aria-current={view === 'public' ? 'page' : undefined}
              onClick={() => setView('public')}
            >
              Travel
            </button>
            <button type="button">Rooms</button>
            <button type="button">Meals</button>
            <button type="button">News</button>
            <button type="button">About</button>
            <button type="button">Contact</button>
          </nav>
        </div>
      </header>

      {view === 'public' && (
        <>
          <section className="page-intro">
            <h1>Explore Available Trips</h1>
            <p>
              This React client retrieves typed trip data from the enhanced
              Spring Boot API and displays it through reusable components.
            </p>
          </section>

          <section className="trip-controls" aria-label="Trip filters">
            <label>
              Sort by
              <select
                value={sort}
                onChange={(event) => setSort(event.target.value)}
              >
                <option value="name">Name</option>
                <option value="price">Price</option>
                <option value="startDate">Start date</option>
                <option value="duration">Duration</option>
              </select>
            </label>

            <label>
              Max price
              <input
                type="number"
                min="0"
                step="50"
                value={maxPrice}
                placeholder="No limit"
                onChange={(event) => setMaxPrice(event.target.value)}
              />
            </label>
          </section>

          {isLoading && <p className="status-message">Loading trips...</p>}

          {errorMessage && (
            <p className="status-message status-message--error">
              {errorMessage}
            </p>
          )}

          {!isLoading && !errorMessage && <TripList trips={publicTrips} />}
        </>
      )}

      {view === 'admin' && (
        <AdminTripManager
          trips={adminTrips}
          onTripsChanged={handleTripsChanged}
          onBackToTravel={() => setView('public')}
        />
      )}
    </main>
  );
}

export default App;
