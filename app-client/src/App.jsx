import { useEffect, useState } from 'react';
import { getTrips } from './api/tripApi';
import TripList from './components/TripList';
import './App.css';

/**
 * Main application component for the enhanced Travlr client.
 *
 * This component retrieves trip data from the Spring Boot API and displays
 * loading, error, empty, and successful data states.
 *
 * @returns {JSX.Element} rendered application
 */
function App() {
  const [trips, setTrips] = useState([]);
  const [sort, setSort] = useState('name');
  const [maxPrice, setMaxPrice] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    let isCurrentRequest = true;

    async function loadTrips() {
      try {
        setIsLoading(true);
        setErrorMessage('');

        const tripData = await getTrips({ sort, maxPrice });

        if (isCurrentRequest) {
          setTrips(tripData);
        }
      } catch (error) {
        if (isCurrentRequest) {
          setErrorMessage(error.message);
          setTrips([]);
        }
      } finally {
        if (isCurrentRequest) {
          setIsLoading(false);
        }
      }
    }

    loadTrips();

    return () => {
      isCurrentRequest = false;
    };
  }, [sort, maxPrice]);

  return (
    <main className="app-shell">
      <header className="site-header">
        <div className="site-header__inner">
          <img
            className="site-logo"
            src="/images/logo.png"
            alt="Travlr Getaways"
          />

          <nav className="site-nav" aria-label="Primary navigation">
            <button type="button">Home</button>
            <button
              type="button"
              className="site-nav__active"
              aria-current="page"
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

      <section className="page-intro">
        <h1>Explore Available Trips</h1>
        <p>
          This React client retrieves typed trip data from the enhanced Spring Boot
          API and displays it through reusable components.
        </p>
      </section>

      <section className="trip-controls" aria-label="Trip filters">
        <label>
          Sort by
          <select value={sort} onChange={(event) => setSort(event.target.value)}>
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

      {!isLoading && !errorMessage && <TripList trips={trips} />}
    </main>
  );
}

export default App;
